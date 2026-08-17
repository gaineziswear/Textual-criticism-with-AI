package com.example.agent

import com.example.engine.LexiconCorroboration
import com.example.engine.LinguisticPipeline
import com.example.model.AncientLanguage
import com.example.model.LanguageCatalog
import com.example.model.WordGloss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AgentResponse(
    val replyText: String,
    val modifiedExclusions: Set<String>? = null,
    val targetedWordGloss: WordGloss? = null,
    val actionTaken: String? = null
)

class InteractiveAnalysisAgent {

    suspend fun interact(
        userQuery: String,
        currentPassage: String,
        currentWordGlosses: List<WordGloss>,
        activeExclusions: Set<String>
    ): AgentResponse = withContext(Dispatchers.Default) {
        val queryLower = userQuery.trim().lowercase()

        // 1. Check for exclusion control commands in conversation
        if (queryLower.contains("exclude ") || queryLower.contains("remove language ") || queryLower.contains("disable ")) {
            val words = queryLower.split(Regex("\\s+"))
            val targetLang = LanguageCatalog.ALL_53_LANGUAGES.firstOrNull { lang ->
                queryLower.contains(lang.canonicalName.lowercase()) || queryLower.contains(lang.id)
            }
            if (targetLang != null) {
                val newExclusions = activeExclusions + targetLang.id
                return@withContext AgentResponse(
                    replyText = "Language **${targetLang.canonicalName}** has been removed from candidate consideration for this session. Words with this script tier will now be evaluated against remaining active candidates or fall through to the root-language fallback chain.",
                    modifiedExclusions = newExclusions,
                    actionTaken = "EXCLUDED_${targetLang.id.uppercase()}"
                )
            }
        }

        if (queryLower.contains("include ") || queryLower.contains("restore ") || queryLower.contains("enable ")) {
            val targetLang = LanguageCatalog.ALL_53_LANGUAGES.firstOrNull { lang ->
                queryLower.contains(lang.canonicalName.lowercase()) || queryLower.contains(lang.id)
            }
            if (targetLang != null && targetLang.id in activeExclusions) {
                val newExclusions = activeExclusions - targetLang.id
                return@withContext AgentResponse(
                    replyText = "Language **${targetLang.canonicalName}** has been restored to active candidate consideration.",
                    modifiedExclusions = newExclusions,
                    actionTaken = "INCLUDED_${targetLang.id.uppercase()}"
                )
            }
        }

        if (queryLower.contains("clear exclusion") || queryLower.contains("reset exclusion")) {
            return@withContext AgentResponse(
                replyText = "All language exclusions have been cleared. All 53 canonical languages are active.",
                modifiedExclusions = emptySet(),
                actionTaken = "RESET_EXCLUSIONS"
            )
        }

        // 2. Check for explicit Root Fallback invocation
        if (queryLower.contains("fallback") || queryLower.contains("root") || queryLower.contains("ancestor")) {
            // Find target word
            val targetGloss = currentWordGlosses.firstOrNull { gloss ->
                queryLower.contains(gloss.cleanedToken.lowercase()) ||
                queryLower.contains("word ${gloss.index + 1}") ||
                queryLower.contains("#${gloss.index + 1}")
            } ?: currentWordGlosses.firstOrNull()

            if (targetGloss != null) {
                val lang = targetGloss.selectedLanguage
                val fallbacks = if (lang != null) LanguageCatalog.getFallbacksFor(lang.id) else emptyList()

                val explanation = buildString {
                    appendLine("### Root-Language Fallback Analysis for [${targetGloss.rawToken}] (Word #${targetGloss.index + 1})")
                    appendLine()
                    if (lang != null) {
                        appendLine("- **Primary Candidate**: ${lang.canonicalName} (${lang.familyBranch})")
                        if (fallbacks.isNotEmpty()) {
                            appendLine("- **Attested Fallback Chain**: ${fallbacks.joinToString(" → ") { it.canonicalName }}")
                            appendLine("- **Linguistic Descent**: Epigraphic forms in ${lang.canonicalName} frequently preserve morphological paradigms attested in ${fallbacks.first().canonicalName}.")
                            appendLine("- **Confidence Policy**: Per §5 specifications, matches resolved via fallback are capped at **LOW** confidence to maintain honest academic rigor.")
                        } else {
                            appendLine("- **Isolate / Unique Branch**: ${lang.canonicalName} has no defensible ancestor/sister fallback in this catalog. Status remains **UNCERTAIN** rather than forcing a speculative classification.")
                        }
                    } else {
                        appendLine("The token has no primary language candidate. Script detection did not yield a single decisive branch.")
                    }
                }

                return@withContext AgentResponse(
                    replyText = explanation,
                    targetedWordGloss = targetGloss,
                    actionTaken = "ROOT_FALLBACK_ANALYSIS"
                )
            }
        }

        // 3. Deep morphological & literal analysis of a word or line
        val specificWord = currentWordGlosses.firstOrNull { gloss ->
            queryLower.contains(gloss.cleanedToken.lowercase()) ||
            queryLower.contains("word ${gloss.index + 1}") ||
            queryLower.contains("#${gloss.index + 1}")
        }

        if (specificWord != null) {
            val dict = specificWord.dictionaryCorroboration
            val analysis = buildString {
                appendLine("### Deep Analysis: Word #${specificWord.index + 1} 「${specificWord.rawToken}」")
                appendLine("- **Transliteration**: `${specificWord.transliteration ?: "—"}`")
                appendLine("- **Detected Script**: ${specificWord.detectedScript} (Tier ${specificWord.scriptTier.tierNumber}: ${specificWord.scriptTier.description})")
                appendLine("- **Assigned Language**: ${specificWord.selectedLanguage?.canonicalName ?: "Uncertain"}")
                appendLine("- **Literal Gloss (EN)**: *${specificWord.literalGlossEn}*")
                appendLine("- **Literal Gloss (FR)**: *${specificWord.literalGlossFr}*")
                appendLine("- **Confidence Level**: **${specificWord.confidence.label}** ${if (specificWord.isFallbackMatch) "(Capped due to Fallback match)" else ""}")
                if (specificWord.fallbackDetails != null) {
                    appendLine("- **Fallback Path**: ${specificWord.fallbackDetails}")
                }
                if (dict != null) {
                    appendLine("- **Lexicon Source**: ${dict.source}")
                    appendLine("- **Lemma**: ${dict.lemma} ${dict.root?.let { " [Root: $it]" } ?: ""}")
                    appendLine("- **Part of Speech**: ${dict.partOfSpeech ?: "Unspecified"}")
                    appendLine("- **Direct Attestation**: ${if (dict.isDirectAttestation) "Verified in Classical Lexicon" else "Inferred"}")
                }
                appendLine()
                appendLine("#### Strict Philological Notes:")
                appendLine("1. Adheres strictly to literal glossing (no theological or interpretive paraphrase).")
                appendLine("2. Surrounding context is considered for grammatical inflection and lemma boundary identification.")
            }
            return@withContext AgentResponse(
                replyText = analysis,
                targetedWordGloss = specificWord,
                actionTaken = "DEEP_WORD_ANALYSIS"
            )
        }

        // 4. General passage analysis
        val response = buildString {
            appendLine("### Glossa Research Deep-Analysis Overview")
            appendLine("Evaluated **${currentWordGlosses.size} tokens** in the active manuscript context:")
            appendLine()
            currentWordGlosses.take(8).forEach { word ->
                val fallbackTag = if (word.isFallbackMatch) " [Fallback]" else ""
                appendLine("- **#${word.index + 1} ${word.rawToken}** (`${word.transliteration ?: ""}`) → **${word.selectedLanguage?.canonicalName ?: "Uncertain"}**${fallbackTag} | EN: *${word.literalGlossEn}* | Conf: ${word.confidence.label}")
            }
            if (currentWordGlosses.size > 8) {
                appendLine("... and ${currentWordGlosses.size - 8} more tokens.")
            }
            appendLine()
            appendLine("You may ask for:")
            appendLine("• Morphological breakdown of any specific word (e.g. *\"Analyze word #2\"*)")
            appendLine("• Scribal error and variant check (invoking Semitic Textual Critic)")
            appendLine("• Language exclusion (e.g. *\"Exclude Thamudic\"*)")
            appendLine("• Root-language fallback evaluation")
        }

        AgentResponse(replyText = response)
    }
}
