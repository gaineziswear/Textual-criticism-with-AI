package com.example.model

enum class VerificationStatus(val label: String) {
    VERIFIED("Verified"),
    PARTIALLY_VERIFIED("Partially verified"),
    NOT_VERIFIED("Not verified")
}

enum class PhilologicalClassification {
    PROBABLE_BORROWING,
    POSSIBLE_BORROWING,
    COGNATE_OR_SHARED_INHERITANCE,
    ORTHOGRAPHIC_SIMILARITY_ONLY,
    UNCERTAIN
}

data class PhilologicalEvidenceRecord(
    val arabicSpan: String,
    val normalizedArabic: String,
    val arabicBaseline: String,
    val candidateLanguage: String,
    val historicalStage: String,
    val nativeForm: String,
    val transliteration: String,
    val lexicalSource: String?,
    val attestation: String?,
    val dateOrPeriod: String?,
    val semanticMatch: String,
    val phonologicalMatch: String,
    val morphologicalMatch: String,
    val chronology: String,
    val directionality: String,
    val alternativeArabicOrProtoSemiticExplanation: String,
    val classification: PhilologicalClassification,
    val verificationStatus: VerificationStatus,
    val confidence: Float,
    val humanVerificationRequired: Boolean = true,
    val sources: List<SourceReference> = emptyList()
)
