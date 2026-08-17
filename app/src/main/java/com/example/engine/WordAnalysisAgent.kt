package com.example.engine

import com.example.model.AgentTraceStep
import com.example.model.Confidence
import com.example.model.EtymologyAnalysis
import com.example.model.EvidenceStatus
import com.example.model.SemiticCognate
import com.example.model.StructuredWordAnalysis
import com.example.model.TraceStatus
import com.example.model.WordGloss
import java.text.Normalizer

object WordAnalysisAgent {
    fun analyze(wordGloss: WordGloss): StructuredWordAnalysis {
        val normalized = Normalizer.normalize(wordGloss.cleanedToken, Normalizer.Form.NFC)
        val codepoints = normalized.codePoints().toArray().map { "U+%04X".format(it) }
        val broadFamily = inferBroadFamily(wordGloss.selectedLanguage?.familyBranch, wordGloss.detectedScript)
        val etymology = buildEtymology(wordGloss, broadFamily)
        val lexicalStatus = when {
            wordGloss.dictionaryCorroboration?.isDirectAttestation == true -> EvidenceStatus.ATTESTED
            wordGloss.isFallbackMatch -> EvidenceStatus.COMPARATIVE
            wordGloss.selectedLanguage == null -> EvidenceStatus.UNCERTAIN
            else -> EvidenceStatus.HYPOTHETICAL
        }
        val trace = listOf(
            AgentTraceStep("Input normalized", TraceStatus.COMPLETE, "NFC normalization applied before linguistic analysis."),
            AgentTraceStep("Script identified", TraceStatus.COMPLETE, wordGloss.detectedScript),
            AgentTraceStep("Broad family considered", TraceStatus.COMPLETE, broadFamily),
            AgentTraceStep("Specific language candidate selected", if (wordGloss.selectedLanguage == null) TraceStatus.WARNING else TraceStatus.COMPLETE, wordGloss.selectedLanguage?.canonicalName ?: "No forced classification"),
            AgentTraceStep("Root/cognate layer separated", TraceStatus.COMPLETE, "Root analysis precedes translation and is labelled ${etymology.evidenceStatus.label}."),
            AgentTraceStep("Literal gloss generated", TraceStatus.COMPLETE, "Translation remains downstream of script/language/root analysis.")
        )
        return StructuredWordAnalysis(
            surfaceForm = wordGloss.rawToken,
            normalizedForm = normalized,
            unicodeCodepoints = codepoints,
            script = wordGloss.detectedScript,
            transliteration = wordGloss.transliteration,
            orthographicVariants = generateOrthographicVariants(normalized),
            broadLanguageFamily = broadFamily,
            language = wordGloss.selectedLanguage?.canonicalName,
            historicalStage = inferHistoricalStage(wordGloss.selectedLanguage?.id),
            dialect = inferDialect(wordGloss.selectedLanguage?.id),
            scriptTradition = wordGloss.selectedLanguage?.scriptName ?: wordGloss.detectedScript,
            morphology = wordGloss.morphologicalBreakdown,
            etymology = etymology,
            lexicalEvidence = lexicalStatus,
            literalGlossEn = wordGloss.literalGlossEn,
            literalGlossFr = wordGloss.literalGlossFr,
            confidence = wordGloss.confidence,
            trace = trace
        )
    }

    private fun inferBroadFamily(branch: String?, script: String): String = when {
        branch?.contains("Semitic", true) == true -> branch.substringBefore("(").trim().ifBlank { "Semitic" }
        branch?.contains("Iranian", true) == true -> "Iranian / Indo-European"
        branch?.contains("Indo", true) == true -> "Indo-European"
        branch?.contains("Egyptian", true) == true || script.contains("Coptic", true) -> "Egyptian / Afroasiatic"
        branch != null -> branch
        else -> "Uncertain broad linguistic environment"
    }

    private fun inferHistoricalStage(id: String?): String? = when (id) {
        "hebrew" -> "Biblical / epigraphic Hebrew possible; exact stage not forced"
        "aramaic" -> "Imperial, Jewish, or later Aramaic possible; stage requires context"
        "syriac" -> "Classical Syriac likely when written in Syriac script"
        "greek" -> "Classical/Koine/Byzantine requires context"
        "arabic" -> "Classical, epigraphic, or dialectal context unresolved"
        else -> null
    }

    private fun inferDialect(id: String?): String? = when (id) {
        "aramaic" -> "Aramaic branch unresolved without source metadata"
        "syriac" -> "Syriac literary tradition"
        else -> null
    }

    private fun buildEtymology(wordGloss: WordGloss, broadFamily: String): EtymologyAnalysis {
        val dict = wordGloss.dictionaryCorroboration
        val root = dict?.root ?: extractSemiticSkeleton(wordGloss.cleanedToken).takeIf { broadFamily.contains("Semitic", true) && it.length in 2..4 }
        val fallback = wordGloss.isFallbackMatch
        val status = when {
            dict?.isDirectAttestation == true -> EvidenceStatus.ATTESTED
            fallback -> EvidenceStatus.COMPARATIVE
            root != null -> EvidenceStatus.HYPOTHETICAL
            else -> EvidenceStatus.UNCERTAIN
        }
        val confidence = if (fallback || status == EvidenceStatus.HYPOTHETICAL) Confidence.LOW else wordGloss.confidence
        return EtymologyAnalysis(
            root = root,
            consonantalSkeleton = root,
            morphologicalPattern = dict?.partOfSpeech ?: if (root != null) "Semitic consonantal skeleton; stem/pattern unresolved" else null,
            affixes = detectAffixes(wordGloss.cleanedToken),
            cognates = emptyList<SemiticCognate>(),
            semanticRange = listOf(wordGloss.literalGlossEn).filter { it != "—" && !it.startsWith("[") },
            evidenceStatus = status,
            confidence = confidence,
            notes = listOfNotNull(
                "Similar word ≠ same root; same root ≠ same meaning; possible cognate ≠ proven borrowing.",
                if (fallback) "Fallback is labelled comparative and confidence is capped at LOW." else null,
                if (dict == null) "No retrieved lexical source; do not treat generated gloss as attested." else null
            )
        )
    }

    private fun extractSemiticSkeleton(token: String): String = token.filter { it in "אבגדהוזחטיכךלמםנןסעפףצץקרשתܐܒܓܕܗܘܙܚܛܝܟܠܡܢܣܥܦܨܩܪܫܬ" }.take(4)

    private fun detectAffixes(token: String): List<String> = buildList {
        if (token.startsWith("ו") || token.startsWith("ܘ")) add("possible conjunction prefix")
        if (token.startsWith("ה")) add("possible article/interrogative prefix")
        if (token.endsWith("ים") || token.endsWith("ין")) add("possible plural suffix")
        if (token.endsWith("א") || token.endsWith("ܐ")) add("possible Aramaic/Syriac emphatic ending")
    }

    private fun generateOrthographicVariants(token: String): List<String> = buildList {
        if (token.contains('ד')) add(token.replace('ד', 'ר'))
        if (token.contains('ר')) add(token.replace('ר', 'ד'))
        if (token.contains('ו')) add(token.replace('ו', 'י'))
        if (token.contains('י')) add(token.replace('י', 'ו'))
    }.distinct()
}
