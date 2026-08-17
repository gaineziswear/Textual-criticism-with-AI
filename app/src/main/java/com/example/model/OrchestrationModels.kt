package com.example.model

enum class OrchestrationStage(
    val stageNumber: Int,
    val title: String,
    val agentName: String,
    val description: String
) {
    IDLE(0, "Awaiting Pipeline Trigger", "System Orchestrator", "Select or enter a manuscript passage to initiate sequential multi-agent analysis."),
    STAGE_1_TRANSLATION_AGENT(1, "Translation & Root-Fallback Agent", "Translation & Morphology Agent", "Performing Unicode script tier classification, dictionary lookup, literal EN/FR glossing, and root-language fallback traversal."),
    STAGE_2_TEXTUAL_CRITIC(2, "Semitic Textual Criticism", "Semitic Textual Critic Agent", "Scanning paleographic hazards (Dalet/Resh, Waw/Yod), scribal haplography/dittography, homoioteleuton, and comparative Semitic cognates."),
    STAGE_3_MODEL_COMPARISON(3, "Multi-Provider AI Service Layer", "Gemini & OpenAI & Open-Source Engine", "Executing multi-model validation across Gemini 3.5 Flash, OpenAI GPT, and deterministic Open-Source philological baseline."),
    STAGE_4_SYNTHESIS(4, "Scholarly Philological Synthesis", "Agent Orchestrator", "Consolidating all agent outputs into an integrated academic research dossier with consensus/divergence metrics."),
    COMPLETED(5, "Sequential Analysis Complete", "Pipeline Complete", "All 4 sequential agent layers executed successfully. Final dossier generated."),
    ERROR(-1, "Pipeline Execution Error", "System Error", "An error occurred during stage execution.")
}

data class StageLog(
    val stage: OrchestrationStage,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long,
    val message: String,
    val tokensProcessed: Int = 0,
    val anomaliesDetected: Int = 0,
    val isSuccess: Boolean = true
)

data class OrchestratedDossier(
    val passage: String,
    val wordGlosses: List<WordGloss>,
    val textualCriticReports: List<TextualCriticReport>,
    val multiProviderComparisons: List<MultiProviderComparisonResult>,
    val overallConsensusSummary: String,
    val overallScholarlyStatus: ScholarlyCertainty,
    val executionLogs: List<StageLog>,
    val totalExecutionTimeMs: Long,
    val excludedLanguages: Set<String>
)
