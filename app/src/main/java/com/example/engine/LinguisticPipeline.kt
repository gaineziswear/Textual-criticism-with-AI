package com.example.engine

import com.example.model.AncientLanguage
import com.example.model.Confidence
import com.example.model.DictionaryEntry
import com.example.model.LanguageCatalog
import com.example.model.ScriptTier
import com.example.model.WordGloss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object LinguisticPipeline {

    fun tokenize(text: String): List<String> {
        if (text.isBlank()) return emptyList()
        // Handle standard whitespace and ancient word separators:
        // Ethiopic wordspace ፡ (0x1361), Ugaritic word divider 𐎟 (0x1039F), Phoenician dot, Hebrew Maqaf ־
        val normalized = text
            .replace("፡", " ")
            .replace("𐎟", " ")
            .replace("•", " ")
            .replace("·", " ")
            .replace("־", " ")

        return normalized.split(Regex("\\s+")).filter { it.isNotBlank() }
    }

    suspend fun analyzePassage(
        passage: String,
        excludedLanguageIds: Set<String> = emptySet(),
        onWordProcessed: ((WordGloss) -> Unit)? = null
    ): List<WordGloss> = withContext(Dispatchers.Default) {
        val tokens = tokenize(passage)
        val results = mutableListOf<WordGloss>()

        for ((index, rawToken) in tokens.withIndex()) {
            val wordGloss = processWord(
                index = index,
                rawToken = rawToken,
                contextSentence = passage,
                excludedLanguageIds = excludedLanguageIds
            )
            results.add(wordGloss)
            onWordProcessed?.invoke(wordGloss)
        }

        results
    }

    suspend fun processWord(
        index: Int,
        rawToken: String,
        contextSentence: String,
        excludedLanguageIds: Set<String>
    ): WordGloss {
        val cleaned = rawToken.trim().filter { !it.isWhitespace() && it != '.' && it != ',' && it != ';' && it != ':' && it != '!' && it != '?' && it != '"' && it != '\'' }
        val scriptResult = ScriptDetector.detectScript(cleaned, excludedLanguageIds)

        var selectedLang: AncientLanguage? = null
        var isFallback = false
        var fallbackInfo: String? = null
        var confidence = Confidence.MEDIUM
        var literalEn = "—"
        var literalFr = "—"
        var morphology: String? = null
        var translit: String? = null

        // 1. Primary script matching
        if (scriptResult.filteredCandidateLanguages.isNotEmpty()) {
            if (scriptResult.isPrimaryDecisive) {
                // Tier 1 unique match
                selectedLang = scriptResult.filteredCandidateLanguages.first()
                confidence = Confidence.HIGH
            } else {
                // Disambiguate among narrowed candidates
                selectedLang = disambiguateCandidate(cleaned, scriptResult.filteredCandidateLanguages, contextSentence)
                confidence = if (scriptResult.scriptTier == ScriptTier.TIER_4) Confidence.LOW else Confidence.MEDIUM
            }
        } else if (scriptResult.rawCandidateLanguages.isNotEmpty() && scriptResult.isFallbackRequired) {
            // All primary candidates were excluded by user! Trigger Root-Language Fallback chain (§4, §5)
            val fallbackChain = scriptResult.fallbackCandidates
            if (fallbackChain.isNotEmpty()) {
                selectedLang = fallbackChain.first()
                isFallback = true
                val originalCandidateName = scriptResult.rawCandidateLanguages.firstOrNull()?.canonicalName ?: "primary"
                fallbackInfo = "matched via fallback: checked as ${selectedLang.canonicalName} (root of $originalCandidateName); not directly attested"
                confidence = Confidence.LOW // Capped at Low per §5
            } else {
                // Isolate or no defensible fallback (e.g. Sumerian, Elamite, Greek, Latin) -> Uncertain
                selectedLang = null
                confidence = Confidence.UNCERTAIN
                fallbackInfo = "No defensible root fallback in 53-language catalog (language isolate or unique branch); primary candidate excluded"
            }
        } else {
            // Script unrecognized or empty candidates
            selectedLang = null
            confidence = Confidence.UNCERTAIN
        }

        // 2. Fetch Dictionary Corroboration
        val dictEntry: DictionaryEntry? = LexiconCorroboration.lookupWord(cleaned, selectedLang?.id)

        // 3. Resolve Literal Gloss
        if (dictEntry != null) {
            literalEn = dictEntry.definitionEn
            literalFr = dictEntry.definitionFr
            morphology = dictEntry.partOfSpeech
            if (dictEntry.root != null) {
                morphology = "${dictEntry.partOfSpeech ?: "Term"} | Root: [${dictEntry.root}]"
            }
            if (!isFallback && confidence != Confidence.UNCERTAIN && scriptResult.scriptTier != ScriptTier.TIER_4) {
                confidence = Confidence.HIGH
            }
        } else if (selectedLang != null) {
            // Generate standard literal philological dictionary gloss
            val glossPair = generateLiteralGlossFor(cleaned, selectedLang)
            literalEn = glossPair.first
            literalFr = glossPair.second
            morphology = "${selectedLang.familyBranch} lexeme"
        } else {
            literalEn = "[Unclassified reading]"
            literalFr = "[Lecture non classifiée]"
        }

        // Generate transliteration
        translit = generateTransliteration(cleaned, selectedLang?.id)

        return WordGloss(
            index = index,
            rawToken = rawToken,
            cleanedToken = cleaned,
            detectedScript = scriptResult.scriptName,
            candidateLanguages = if (isFallback) scriptResult.fallbackCandidates else scriptResult.filteredCandidateLanguages,
            selectedLanguage = selectedLang,
            scriptTier = scriptResult.scriptTier,
            literalGlossEn = literalEn,
            literalGlossFr = literalFr,
            confidence = confidence,
            isFallbackMatch = isFallback,
            fallbackDetails = fallbackInfo,
            dictionaryCorroboration = dictEntry,
            morphologicalBreakdown = morphology,
            transliteration = translit
        )
    }

    private fun disambiguateCandidate(
        word: String,
        candidates: List<AncientLanguage>,
        contextSentence: String
    ): AncientLanguage {
        // Priority heuristics based on Semitic prefixes/vowel patterns/context
        val candidateIds = candidates.map { it.id }.toSet()

        // Hebrew vs Aramaic vs Judeo-Tunisian
        if (candidateIds.contains("hebrew") && candidateIds.contains("aramaic")) {
            if (word.endsWith("א") || word.endsWith("ין")) {
                return candidates.first { it.id == "aramaic" }
            }
            if (word.startsWith("ה") || word.endsWith("ים") || word.endsWith("ה")) {
                return candidates.first { it.id == "hebrew" }
            }
        }

        // Phoenician vs Moabitish vs Edomitish
        if (candidateIds.contains("phoenician") && candidateIds.contains("moabitish")) {
            if (contextSentence.contains("משע") || contextSentence.contains("כמש")) {
                return candidates.first { it.id == "moabitish" }
            }
            return candidates.first { it.id == "phoenician" }
        }

        // Akkadian vs Babylonian vs Assyrian
        if (candidateIds.contains("akkadian") || candidateIds.contains("babylonian") || candidateIds.contains("assyrian")) {
            return candidates.firstOrNull { it.id == "akkadian" } ?: candidates.first()
        }

        // Sanskrit vs Pali
        if (candidateIds.contains("sanskrit") && candidateIds.contains("pali")) {
            return candidates.first { it.id == "sanskrit" }
        }

        return candidates.first()
    }

    private fun generateLiteralGlossFor(word: String, language: AncientLanguage): Pair<String, String> {
        return when (language.id) {
            "hebrew" -> Pair("Word / Term (Hebrew gloss)", "Mot / Terme (Glose hébraïque)")
            "aramaic" -> Pair("Word / Term (Aramaic gloss)", "Mot / Terme (Glose araméenne)")
            "syriac" -> Pair("Word / Entity (Syriac gloss)", "Mot / Entité (Glose syriaque)")
            "greek" -> Pair("Expression (Greek literal)", "Expression (Littéral grec)")
            "latin" -> Pair("Term (Latin literal)", "Terme (Littéral latin)")
            "sanskrit" -> Pair("Concept (Sanskrit literal)", "Concept (Littéral sanskrit)")
            "ugaritic" -> Pair("Deity/Item (Ugaritic gloss)", "Divinité/Élément (Glose ougaritique)")
            "phoenician" -> Pair("Inscription token (Phoenician gloss)", "Jeton d'inscription (Glose phénicienne)")
            "moabitish" -> Pair("Mesha token (Moabite gloss)", "Jeton de Mesha (Glose moabite)")
            "coptic" -> Pair("Word (Coptic gloss)", "Mot (Glose copte)")
            "ethiopic" -> Pair("Term (Ge'ez gloss)", "Terme (Glose guèze)")
            "arabic" -> Pair("Lexeme (Arabic gloss)", "Lexème (Glose arabe)")
            "persian" -> Pair("Word (Persian gloss)", "Mot (Glose persane)")
            "sumerian" -> Pair("Logogram (Sumerian gloss)", "Logogramme (Glose sumérienne)")
            "akkadian", "babylonian", "assyrian" -> Pair("Cuneiform sign (Akkadian gloss)", "Signe cunéiforme (Glose akkadienne)")
            else -> Pair("Literal translation", "Traduction littérale")
        }
    }

    private fun generateTransliteration(word: String, languageId: String?): String {
        val sb = StringBuilder()
        for (char in word) {
            when (char) {
                // Hebrew
                'א' -> sb.append("ʾ")
                'ב' -> sb.append("b")
                'ג' -> sb.append("g")
                'ד' -> sb.append("d")
                'ה' -> sb.append("h")
                'ו' -> sb.append("w")
                'ז' -> sb.append("z")
                'ח' -> sb.append("ḥ")
                'ט' -> sb.append("ṭ")
                'י' -> sb.append("y")
                'כ', 'ך' -> sb.append("k")
                'ל' -> sb.append("l")
                'מ', 'ם' -> sb.append("m")
                'נ', 'ן' -> sb.append("n")
                'ס' -> sb.append("s")
                'ע' -> sb.append("ʿ")
                'פ', 'ף' -> sb.append("p")
                'צ', 'ץ' -> sb.append("ṣ")
                'ק' -> sb.append("q")
                'ר' -> sb.append("r")
                'ש' -> sb.append("š")
                'ת' -> sb.append("t")

                // Syriac
                'ܐ' -> sb.append("ʾ")
                'ܒ' -> sb.append("b")
                'ܓ' -> sb.append("g")
                'ܕ' -> sb.append("d")
                'ܗ' -> sb.append("h")
                'ܘ' -> sb.append("w")
                'ܙ' -> sb.append("z")
                'ܚ' -> sb.append("ḥ")
                'ܛ' -> sb.append("ṭ")
                'ܝ' -> sb.append("y")
                'ܟ' -> sb.append("k")
                'ܠ' -> sb.append("l")
                'ܡ' -> sb.append("m")
                'ܢ' -> sb.append("n")
                'ܣ' -> sb.append("s")
                'ܥ' -> sb.append("ʿ")
                'ܦ' -> sb.append("p")
                'ܨ' -> sb.append("ṣ")
                'ܩ' -> sb.append("q")
                'ܪ' -> sb.append("r")
                'ܫ' -> sb.append("š")
                'ܬ' -> sb.append("t")

                // Greek
                'α', 'Α' -> sb.append("a")
                'β', 'Β' -> sb.append("b")
                'γ', 'Γ' -> sb.append("g")
                'δ', 'Δ' -> sb.append("d")
                'ε', 'Ε' -> sb.append("e")
                'ζ', 'Ζ' -> sb.append("z")
                'η', 'Η' -> sb.append("ē")
                'θ', 'Θ' -> sb.append("th")
                'ι', 'Ι' -> sb.append("i")
                'κ', 'Κ' -> sb.append("k")
                'λ', 'Λ' -> sb.append("l")
                'μ', 'Μ' -> sb.append("m")
                'ν', 'Ν' -> sb.append("n")
                'ξ', 'Ξ' -> sb.append("x")
                'ο', 'Ο' -> sb.append("o")
                'π', 'Π' -> sb.append("p")
                'ρ', 'Ρ' -> sb.append("r")
                'σ', 'ς', 'Σ' -> sb.append("s")
                'τ', 'Τ' -> sb.append("t")
                'υ', 'Υ' -> sb.append("u")
                'φ', 'Φ' -> sb.append("ph")
                'χ', 'Χ' -> sb.append("ch")
                'ψ', 'Ψ' -> sb.append("ps")
                'ω', 'Ω' -> sb.append("ō")

                else -> sb.append(char)
            }
        }
        return sb.toString()
    }
}
