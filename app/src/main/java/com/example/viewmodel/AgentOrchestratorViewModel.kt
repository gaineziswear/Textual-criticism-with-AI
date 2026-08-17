package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.agent.InteractiveAnalysisAgent
import com.example.agent.SemiticTextualCriticAgent
import com.example.data.GlossaDatabase
import com.example.data.ManuscriptRecord
import com.example.data.ScholarNoteRecord
import com.example.engine.LinguisticPipeline
import com.example.engine.MultiProviderComparisonEngine
import com.example.model.MultiProviderComparisonResult
import com.example.model.OrchestratedDossier
import com.example.model.OrchestrationStage
import com.example.model.ScholarlyCertainty
import com.example.model.StageLog
import com.example.model.TextualCriticReport
import com.example.model.WordGloss
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AgentOrchestratorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        GlossaDatabase::class.java,
        "glossa_academic_v1.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.glossaDao()

    // Agents
    private val interactiveAgent = InteractiveAnalysisAgent()
    private val semiticCriticAgent = SemiticTextualCriticAgent()

    // Orchestration Flow State
    private val _currentStage = MutableStateFlow(OrchestrationStage.IDLE)
    val currentStage: StateFlow<OrchestrationStage> = _currentStage.asStateFlow()

    private val _stageProgress = MutableStateFlow(0f)
    val stageProgress: StateFlow<Float> = _stageProgress.asStateFlow()

    private val _isOrchestrating = MutableStateFlow(false)
    val isOrchestrating: StateFlow<Boolean> = _isOrchestrating.asStateFlow()

    private val _isStepByStepMode = MutableStateFlow(false)
    val isStepByStepMode: StateFlow<Boolean> = _isStepByStepMode.asStateFlow()

    private val _stageLogs = MutableStateFlow<List<StageLog>>(emptyList())
    val stageLogs: StateFlow<List<StageLog>> = _stageLogs.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Pipeline Artifacts
    private val _activePassage = MutableStateFlow("")
    val activePassage: StateFlow<String> = _activePassage.asStateFlow()

    private val _activeExcludedLanguages = MutableStateFlow<Set<String>>(emptySet())
    val activeExcludedLanguages: StateFlow<Set<String>> = _activeExcludedLanguages.asStateFlow()

    private val _wordGlosses = MutableStateFlow<List<WordGloss>>(emptyList())
    val wordGlosses: StateFlow<List<WordGloss>> = _wordGlosses.asStateFlow()

    private val _textualCriticReports = MutableStateFlow<List<TextualCriticReport>>(emptyList())
    val textualCriticReports: StateFlow<List<TextualCriticReport>> = _textualCriticReports.asStateFlow()

    private val _multiProviderResults = MutableStateFlow<List<MultiProviderComparisonResult>>(emptyList())
    val multiProviderResults: StateFlow<List<MultiProviderComparisonResult>> = _multiProviderResults.asStateFlow()

    private val _finalDossier = MutableStateFlow<OrchestratedDossier?>(null)
    val finalDossier: StateFlow<OrchestratedDossier?> = _finalDossier.asStateFlow()

    private var orchestrationJob: Job? = null
    private var openAiApiKey: String? = null

    fun setOpenAiApiKey(key: String?) {
        openAiApiKey = key?.ifBlank { null }
    }

    fun setStepByStepMode(enabled: Boolean) {
        _isStepByStepMode.value = enabled
    }

    /**
     * Executes the complete 4-stage sequential analysis pipeline automatically.
     */
    fun startSequentialPipeline(
        passage: String,
        excludedLanguages: Set<String>,
        apiKey: String? = null
    ) {
        if (passage.isBlank()) return
        openAiApiKey = apiKey?.ifBlank { null }
        _activePassage.value = passage
        _activeExcludedLanguages.value = excludedLanguages
        _isStepByStepMode.value = false
        _errorMessage.value = null
        _stageLogs.value = emptyList()
        _finalDossier.value = null

        orchestrationJob?.cancel()
        orchestrationJob = viewModelScope.launch {
            _isOrchestrating.value = true
            val pipelineStartTime = System.currentTimeMillis()

            try {
                // STAGE 1: Translation & Root-Fallback Agent
                executeStage1TranslationAgent(passage, excludedLanguages)

                // STAGE 2: Semitic Textual Critic Agent
                executeStage2TextualCriticAgent()

                // STAGE 3: Multi-Provider AI Service Layer
                executeStage3MultiProviderEvaluation(passage)

                // STAGE 4: Synthesis & Final Dossier
                executeStage4Synthesis(pipelineStartTime, passage, excludedLanguages)

                _currentStage.value = OrchestrationStage.COMPLETED
                _stageProgress.value = 1.0f
            } catch (e: Exception) {
                _currentStage.value = OrchestrationStage.ERROR
                _errorMessage.value = "Pipeline orchestration failed: ${e.localizedMessage ?: "Unknown error"}"
                logStage(
                    OrchestrationStage.ERROR,
                    durationMs = 0,
                    message = "Error: ${e.localizedMessage}",
                    isSuccess = false
                )
            } finally {
                _isOrchestrating.value = false
            }
        }
    }

    /**
     * Prepares step-by-step sequential pipeline for manual step advance.
     */
    fun startStepByStepPipeline(
        passage: String,
        excludedLanguages: Set<String>,
        apiKey: String? = null
    ) {
        if (passage.isBlank()) return
        openAiApiKey = apiKey?.ifBlank { null }
        _activePassage.value = passage
        _activeExcludedLanguages.value = excludedLanguages
        _isStepByStepMode.value = true
        _errorMessage.value = null
        _stageLogs.value = emptyList()
        _wordGlosses.value = emptyList()
        _textualCriticReports.value = emptyList()
        _multiProviderResults.value = emptyList()
        _finalDossier.value = null
        _currentStage.value = OrchestrationStage.IDLE
        _stageProgress.value = 0f
    }

    /**
     * Advances to the next sequential stage in step-by-step mode.
     */
    fun advanceNextStage() {
        orchestrationJob?.cancel()
        orchestrationJob = viewModelScope.launch {
            _isOrchestrating.value = true
            val passage = _activePassage.value
            val excluded = _activeExcludedLanguages.value

            try {
                when (_currentStage.value) {
                    OrchestrationStage.IDLE, OrchestrationStage.ERROR -> {
                        executeStage1TranslationAgent(passage, excluded)
                    }
                    OrchestrationStage.STAGE_1_TRANSLATION_AGENT -> {
                        executeStage2TextualCriticAgent()
                    }
                    OrchestrationStage.STAGE_2_TEXTUAL_CRITIC -> {
                        executeStage3MultiProviderEvaluation(passage)
                    }
                    OrchestrationStage.STAGE_3_MODEL_COMPARISON -> {
                        val startTime = _stageLogs.value.firstOrNull()?.timestamp ?: System.currentTimeMillis()
                        executeStage4Synthesis(startTime, passage, excluded)
                        _currentStage.value = OrchestrationStage.COMPLETED
                        _stageProgress.value = 1.0f
                    }
                    OrchestrationStage.STAGE_4_SYNTHESIS, OrchestrationStage.COMPLETED -> {
                        // Already completed
                    }
                }
            } catch (e: Exception) {
                _currentStage.value = OrchestrationStage.ERROR
                _errorMessage.value = e.localizedMessage
            } finally {
                _isOrchestrating.value = false
            }
        }
    }

    private suspend fun executeStage1TranslationAgent(passage: String, excludedLanguages: Set<String>) {
        _currentStage.value = OrchestrationStage.STAGE_1_TRANSLATION_AGENT
        _stageProgress.value = 0.25f
        val stageStart = System.currentTimeMillis()

        val glosses = LinguisticPipeline.analyzePassage(passage, excludedLanguages)
        _wordGlosses.value = glosses

        val fallbackCount = glosses.count { it.isFallbackMatch }
        val duration = System.currentTimeMillis() - stageStart

        logStage(
            OrchestrationStage.STAGE_1_TRANSLATION_AGENT,
            durationMs = duration,
            message = "Translation Agent parsed ${glosses.size} tokens across 53 languages ($fallbackCount root-language fallbacks triggered).",
            tokensProcessed = glosses.size
        )

        delay(150) // Smooth UI pacing
    }

    private suspend fun executeStage2TextualCriticAgent() {
        _currentStage.value = OrchestrationStage.STAGE_2_TEXTUAL_CRITIC
        _stageProgress.value = 0.50f
        val stageStart = System.currentTimeMillis()

        val glosses = _wordGlosses.value
        val reports = mutableListOf<TextualCriticReport>()
        var totalAnomalies = 0

        for (gloss in glosses) {
            val report = semiticCriticAgent.analyzeTextCritical(gloss, glosses)
            reports.add(report)
            totalAnomalies += report.potentialErrors.size
        }

        _textualCriticReports.value = reports
        val duration = System.currentTimeMillis() - stageStart

        logStage(
            OrchestrationStage.STAGE_2_TEXTUAL_CRITIC,
            durationMs = duration,
            message = "Semitic Textual Critic evaluated ${reports.size} token environments; identified $totalAnomalies paleographic/scribal hazard considerations.",
            tokensProcessed = reports.size,
            anomaliesDetected = totalAnomalies
        )

        delay(150)
    }

    private suspend fun executeStage3MultiProviderEvaluation(passage: String) {
        _currentStage.value = OrchestrationStage.STAGE_3_MODEL_COMPARISON
        _stageProgress.value = 0.75f
        val stageStart = System.currentTimeMillis()

        val glosses = _wordGlosses.value
        val comparisons = mutableListOf<MultiProviderComparisonResult>()

        // For efficiency & clarity, evaluate the primary tokens (or up to 4 significant tokens)
        val targetGlosses = if (glosses.size <= 4) glosses else glosses.take(4)

        for (gloss in targetGlosses) {
            val comp = MultiProviderComparisonEngine.compareWord(
                wordGloss = gloss,
                contextSentence = passage,
                openAiApiKey = openAiApiKey
            )
            comparisons.add(comp)
        }

        _multiProviderResults.value = comparisons
        val duration = System.currentTimeMillis() - stageStart

        logStage(
            OrchestrationStage.STAGE_3_MODEL_COMPARISON,
            durationMs = duration,
            message = "Multi-Provider AI service executed cross-evaluation across Gemini, OpenAI, and Open-Source Baseline on ${comparisons.size} tokens.",
            tokensProcessed = comparisons.size
        )

        delay(150)
    }

    private suspend fun executeStage4Synthesis(
        pipelineStartTime: Long,
        passage: String,
        excludedLanguages: Set<String>
    ) {
        _currentStage.value = OrchestrationStage.STAGE_4_SYNTHESIS
        _stageProgress.value = 0.90f
        val stageStart = System.currentTimeMillis()

        val glosses = _wordGlosses.value
        val criticReports = _textualCriticReports.value
        val providerResults = _multiProviderResults.value

        val totalAnomalies = criticReports.sumOf { it.potentialErrors.size }
        val overallStatus = when {
            totalAnomalies > 2 -> ScholarlyCertainty.OPEN_QUESTION
            glosses.any { it.isFallbackMatch } -> ScholarlyCertainty.MINORITY_POSITION
            else -> ScholarlyCertainty.SCHOLARLY_CONSENSUS
        }

        val consensusSummary = buildString {
            val classifiedCount = glosses.count { it.selectedLanguage != null }
            append("Sequential Orchestration Synthesis: $classifiedCount/${glosses.size} tokens classified with strict literal glossing. ")
            if (totalAnomalies > 0) {
                append("Textual critic flagged $totalAnomalies paleographic considerations. ")
            }
            if (providerResults.isNotEmpty()) {
                val agreements = providerResults.map { it.agreementSummary }
                append("Provider matrix: ${agreements.firstOrNull() ?: "Consensus verified."}")
            }
        }

        val totalDuration = System.currentTimeMillis() - pipelineStartTime

        val dossier = OrchestratedDossier(
            passage = passage,
            wordGlosses = glosses,
            textualCriticReports = criticReports,
            multiProviderComparisons = providerResults,
            overallConsensusSummary = consensusSummary,
            overallScholarlyStatus = overallStatus,
            executionLogs = _stageLogs.value,
            totalExecutionTimeMs = totalDuration,
            excludedLanguages = excludedLanguages
        )

        _finalDossier.value = dossier
        val duration = System.currentTimeMillis() - stageStart

        logStage(
            OrchestrationStage.STAGE_4_SYNTHESIS,
            durationMs = duration,
            message = "Scholarly Synthesis compiled comprehensive dossier in ${totalDuration}ms. Status: ${overallStatus.label}.",
            tokensProcessed = glosses.size,
            anomaliesDetected = totalAnomalies
        )
    }

    private fun logStage(
        stage: OrchestrationStage,
        durationMs: Long,
        message: String,
        tokensProcessed: Int = 0,
        anomaliesDetected: Int = 0,
        isSuccess: Boolean = true
    ) {
        val newLog = StageLog(
            stage = stage,
            durationMs = durationMs,
            message = message,
            tokensProcessed = tokensProcessed,
            anomaliesDetected = anomaliesDetected,
            isSuccess = isSuccess
        )
        _stageLogs.value = _stageLogs.value + newLog
    }

    fun saveDossierToArchive(title: String, notes: String) {
        val dossier = _finalDossier.value ?: return
        viewModelScope.launch {
            val record = ManuscriptRecord(
                title = title.ifBlank { "Orchestrated Collation (${System.currentTimeMillis() % 10000})" },
                originalText = dossier.passage,
                sourceDescription = "Orchestrated 4-stage pipeline analysis. ${notes.ifBlank { dossier.overallConsensusSummary }}",
                excludedLanguages = dossier.excludedLanguages.joinToString(","),
                analysisSummary = "${dossier.wordGlosses.size} tokens, ${dossier.overallScholarlyStatus.label}"
            )
            val id = dao.insertManuscript(record)

            // Save textual critic anomaly notes
            dossier.textualCriticReports.forEach { report ->
                report.potentialErrors.forEach { anomaly ->
                    dao.insertNote(
                        ScholarNoteRecord(
                            manuscriptId = id,
                            token = report.token,
                            wordIndex = report.wordIndex,
                            noteType = anomaly.errorType,
                            content = "${anomaly.description} | Variant: ${anomaly.possibleAlternativeReading}",
                            authorAgent = "AgentOrchestrator:SemiticCritic"
                        )
                    )
                }
            }
        }
    }

    fun resetPipeline() {
        orchestrationJob?.cancel()
        _isOrchestrating.value = false
        _currentStage.value = OrchestrationStage.IDLE
        _stageProgress.value = 0f
        _stageLogs.value = emptyList()
        _wordGlosses.value = emptyList()
        _textualCriticReports.value = emptyList()
        _multiProviderResults.value = emptyList()
        _finalDossier.value = null
        _errorMessage.value = null
    }
}
