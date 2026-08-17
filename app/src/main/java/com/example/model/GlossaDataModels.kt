package com.example.model

data class WordGloss(
    val index: Int,
    val rawToken: String,
    val cleanedToken: String,
    val detectedScript: String,
    val candidateLanguages: List<AncientLanguage>,
    val selectedLanguage: AncientLanguage?,
    val scriptTier: ScriptTier,
    val literalGlossEn: String,
    val literalGlossFr: String,
    val confidence: Confidence,
    val isFallbackMatch: Boolean = false,
    val fallbackDetails: String? = null,
    val dictionaryCorroboration: DictionaryEntry? = null,
    val morphologicalBreakdown: String? = null,
    val textualNotes: List<String> = emptyList(),
    val transliteration: String? = null
)

data class DictionaryEntry(
    val source: String,
    val lemma: String,
    val root: String?,
    val partOfSpeech: String?,
    val definitionEn: String,
    val definitionFr: String,
    val isDirectAttestation: Boolean = true
)

data class TextualCriticReport(
    val wordIndex: Int,
    val token: String,
    val primaryReading: String,
    val potentialErrors: List<ScribalAnomaly>,
    val comparativeRoots: List<SemiticCognate>,
    val transmissionStatus: ScholarlyCertainty,
    val criticCommentary: String
)

data class ScribalAnomaly(
    val errorType: String, // e.g. "Haplography", "Dittography", "Homoioteleuton", "Visual Confusability"
    val description: String,
    val possibleAlternativeReading: String,
    val plausibility: String // "Plausible", "Attested Variant", "Speculative"
)

data class SemiticCognate(
    val language: String,
    val form: String,
    val meaning: String,
    val protoSemiticRoot: String
)

enum class ScholarlyCertainty(val label: String) {
    ESTABLISHED_FACT("Established Reading"),
    SCHOLARLY_CONSENSUS("Scholarly Consensus"),
    MINORITY_POSITION("Minority Position / Variant"),
    OPEN_QUESTION("Open Question / Disputed")
}

data class ProviderComparisonItem(
    val providerName: String,
    val modelName: String,
    val detectedLanguage: String,
    val literalGlossEn: String,
    val literalGlossFr: String,
    val confidence: Confidence,
    val grammaticalAnalysis: String,
    val isFallbackUsed: Boolean,
    val statusMessage: String = "Success"
)

data class MultiProviderComparisonResult(
    val token: String,
    val contextSentence: String,
    val providers: List<ProviderComparisonItem>,
    val agreementSummary: String,
    val consensusLanguage: String?,
    val consensusGlossEn: String?
)
