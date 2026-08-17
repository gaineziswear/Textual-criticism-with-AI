package com.example.model

enum class EvidenceStatus(val label: String) {
    ATTESTED("Attested"),
    COMPARATIVE("Comparative"),
    RECONSTRUCTED("Reconstructed"),
    HYPOTHETICAL("Hypothetical"),
    UNCERTAIN("Uncertain")
}

data class SourceReference(
    val id: String,
    val title: String,
    val author: String? = null,
    val publication: String? = null,
    val date: String? = null,
    val language: String? = null,
    val sourceType: String,
    val url: String? = null,
    val page: String? = null,
    val edition: String? = null,
    val reference: String? = null,
    val reliability: String = "unverified"
)

data class AgentTraceStep(
    val label: String,
    val status: TraceStatus,
    val detail: String
)

enum class TraceStatus { COMPLETE, WARNING, SKIPPED }

data class EtymologyAnalysis(
    val root: String?,
    val consonantalSkeleton: String?,
    val morphologicalPattern: String?,
    val affixes: List<String>,
    val cognates: List<SemiticCognate>,
    val semanticRange: List<String>,
    val evidenceStatus: EvidenceStatus,
    val confidence: Confidence,
    val notes: List<String>
)

data class StructuredWordAnalysis(
    val surfaceForm: String,
    val normalizedForm: String,
    val unicodeCodepoints: List<String>,
    val script: String,
    val transliteration: String?,
    val orthographicVariants: List<String>,
    val broadLanguageFamily: String,
    val language: String?,
    val historicalStage: String?,
    val dialect: String?,
    val scriptTradition: String,
    val morphology: String?,
    val etymology: EtymologyAnalysis,
    val lexicalEvidence: EvidenceStatus,
    val literalGlossEn: String,
    val literalGlossFr: String,
    val confidence: Confidence,
    val trace: List<AgentTraceStep>,
    val sources: List<SourceReference> = emptyList()
)

data class ManuscriptWitnessInput(
    val siglum: String,
    val text: String,
    val date: String? = null,
    val provenance: String? = null,
    val scriptType: String? = null,
    val source: SourceReference? = null
)

enum class VariantType { ADDITION, OMISSION, SUBSTITUTION, TRANSPOSITION, ORTHOGRAPHIC, AGREEMENT }

data class VariantUnit(
    val position: Int,
    val readingsByWitness: Map<String, String?>,
    val variantType: VariantType,
    val possibleScribalPhenomena: List<String>,
    val significanceScore: Int,
    val confidence: Confidence,
    val status: EvidenceStatus = EvidenceStatus.HYPOTHETICAL
)

data class ManuscriptComparisonResult(
    val witnesses: List<ManuscriptWitnessInput>,
    val alignedUnits: List<VariantUnit>,
    val assessment: String,
    val trace: List<AgentTraceStep>
)

data class ContentVerificationResult(
    val status: ContentStatus,
    val findings: List<String>,
    val unsupportedClaims: List<String>,
    val requiredQualifications: List<String>
)

enum class ContentStatus { SUPPORTED, REQUIRES_QUALIFICATION, UNSUPPORTED }

data class VideoScript(
    val title: String,
    val hook: String,
    val narration: String,
    val onScreenText: List<String>,
    val visualSuggestions: List<String>,
    val manuscriptVisual: String,
    val bRoll: List<String>,
    val aiImagePrompt: String,
    val transition: String,
    val cta: String,
    val verification: ContentVerificationResult
)
