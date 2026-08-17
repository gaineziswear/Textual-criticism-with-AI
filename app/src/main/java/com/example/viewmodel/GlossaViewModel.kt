package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.agent.InteractiveAnalysisAgent
import com.example.agent.SemiticTextualCriticAgent
import com.example.data.AgentChatRecord
import com.example.data.GlossaDatabase
import com.example.data.ManuscriptRecord
import com.example.data.SampleManuscripts
import com.example.data.ScholarNoteRecord
import com.example.engine.LinguisticPipeline
import com.example.engine.MultiProviderComparisonEngine
import com.example.model.AncientLanguage
import com.example.model.LanguageCatalog
import com.example.model.MultiProviderComparisonResult
import com.example.model.TextualCriticReport
import com.example.model.WordGloss
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GlossaViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        GlossaDatabase::class.java,
        "glossa_academic_v1.db"
    ).fallbackToDestructiveMigration().build()

    private val dao = db.glossaDao()

    val savedManuscripts: StateFlow<List<ManuscriptRecord>> = dao.getAllManuscripts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatRecords: StateFlow<List<AgentChatRecord>> = dao.getAllChatRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pipeline State
    private val _inputText = MutableStateFlow(SampleManuscripts.ALL_SAMPLES[0].originalText)
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _wordGlosses = MutableStateFlow<List<WordGloss>>(emptyList())
    val wordGlosses: StateFlow<List<WordGloss>> = _wordGlosses.asStateFlow()

    private val _excludedLanguageIds = MutableStateFlow<Set<String>>(emptySet())
    val excludedLanguageIds: StateFlow<Set<String>> = _excludedLanguageIds.asStateFlow()

    // Inspection State
    private val _selectedWordGloss = MutableStateFlow<WordGloss?>(null)
    val selectedWordGloss: StateFlow<WordGloss?> = _selectedWordGloss.asStateFlow()

    private val _textualCriticReport = MutableStateFlow<TextualCriticReport?>(null)
    val textualCriticReport: StateFlow<TextualCriticReport?> = _textualCriticReport.asStateFlow()

    private val _multiProviderResult = MutableStateFlow<MultiProviderComparisonResult?>(null)
    val multiProviderResult: StateFlow<MultiProviderComparisonResult?> = _multiProviderResult.asStateFlow()

    private val _isComparingProviders = MutableStateFlow(false)
    val isComparingProviders: StateFlow<Boolean> = _isComparingProviders.asStateFlow()

    // Agent Studio State
    private val _selectedAgent = MutableStateFlow("INTERACTIVE") // "INTERACTIVE", "SEMITIC_CRITIC", "OPEN_SOURCE"
    val selectedAgent: StateFlow<String> = _selectedAgent.asStateFlow()

    private val _agentTyping = MutableStateFlow(false)
    val agentTyping: StateFlow<Boolean> = _agentTyping.asStateFlow()

    private val _openAiKey = MutableStateFlow("")
    val openAiKey: StateFlow<String> = _openAiKey.asStateFlow()

    private val interactiveAgent = InteractiveAnalysisAgent()
    private val semiticCriticAgent = SemiticTextualCriticAgent()

    init {
        // Initial run
        analyzePassage()
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun setOpenAiKey(key: String) {
        _openAiKey.value = key
    }

    fun setSelectedAgent(agent: String) {
        _selectedAgent.value = agent
    }

    fun toggleLanguageExclusion(langId: String) {
        val current = _excludedLanguageIds.value
        val updated = if (langId in current) current - langId else current + langId
        _excludedLanguageIds.value = updated
        analyzePassage()
    }

    fun clearAllExclusions() {
        _excludedLanguageIds.value = emptySet()
        analyzePassage()
    }

    fun selectWordForDetail(gloss: WordGloss?) {
        _selectedWordGloss.value = gloss
        if (gloss != null) {
            runTextualCriticOnWord(gloss)
        } else {
            _textualCriticReport.value = null
            _multiProviderResult.value = null
        }
    }

    fun loadSample(sample: com.example.data.SampleManuscript) {
        _inputText.value = sample.originalText
        analyzePassage()
    }

    fun analyzePassage() {
        val text = _inputText.value
        if (text.isBlank()) {
            _wordGlosses.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isAnalyzing.value = true
            try {
                val results = LinguisticPipeline.analyzePassage(
                    passage = text,
                    excludedLanguageIds = _excludedLanguageIds.value
                )
                _wordGlosses.value = results
                if (_selectedWordGloss.value != null) {
                    val reselected = results.firstOrNull { it.index == _selectedWordGloss.value?.index }
                    _selectedWordGloss.value = reselected
                    if (reselected != null) {
                        runTextualCriticOnWord(reselected)
                    }
                }
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun runTextualCriticOnWord(wordGloss: WordGloss) {
        viewModelScope.launch {
            val report = semiticCriticAgent.analyzeTextCritical(wordGloss, _wordGlosses.value)
            _textualCriticReport.value = report
        }
    }

    fun runMultiProviderComparison(wordGloss: WordGloss) {
        viewModelScope.launch {
            _isComparingProviders.value = true
            try {
                val result = MultiProviderComparisonEngine.compareWord(
                    wordGloss = wordGloss,
                    contextSentence = _inputText.value,
                    openAiApiKey = _openAiKey.value.ifBlank { null }
                )
                _multiProviderResult.value = result
            } finally {
                _isComparingProviders.value = false
            }
        }
    }

    fun sendAgentMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return

        viewModelScope.launch {
            _agentTyping.value = true

            val promptRecord = AgentChatRecord(
                agentType = _selectedAgent.value,
                userPrompt = userPrompt,
                agentResponse = "",
                contextToken = _selectedWordGloss.value?.rawToken
            )

            val reply = when (_selectedAgent.value) {
                "SEMITIC_CRITIC" -> {
                    val target = _selectedWordGloss.value ?: _wordGlosses.value.firstOrNull()
                    if (target != null) {
                        val report = semiticCriticAgent.analyzeTextCritical(target, _wordGlosses.value)
                        buildString {
                            appendLine("### Semitic Textual Criticism Report")
                            appendLine("Target Token: **${target.rawToken}** (Language: ${target.selectedLanguage?.canonicalName ?: "Semitic continuum"})")
                            appendLine("Scholarly Status: **${report.transmissionStatus.label}**")
                            appendLine()
                            if (report.potentialErrors.isNotEmpty()) {
                                appendLine("#### Paleographic & Scribal Considerations:")
                                report.potentialErrors.forEach { anomaly ->
                                    appendLine("• **${anomaly.errorType}**: ${anomaly.description} (Proposed variant: `${anomaly.possibleAlternativeReading}`)")
                                }
                            }
                            if (report.comparativeRoots.isNotEmpty()) {
                                appendLine()
                                appendLine("#### Comparative Semitic Cognates & Sound Laws:")
                                report.comparativeRoots.forEach { cognate ->
                                    appendLine("• **${cognate.language}**: `${cognate.form}` → *${cognate.meaning}* [Proto-Semitic: `${cognate.protoSemiticRoot}`]")
                                }
                            }
                            appendLine()
                            appendLine("*This evaluation adheres strictly to textual transmission and manuscript collation without theological paraphrase.*")
                        }
                    } else {
                        "Please enter or select a manuscript passage with Semitic tokens to analyze."
                    }
                }
                "OPEN_SOURCE" -> {
                    val target = _selectedWordGloss.value ?: _wordGlosses.value.firstOrNull()
                    if (target != null) {
                        "**[Open-Source Baseline Analysis]**\nToken: `${target.rawToken}`\nScript: ${target.detectedScript}\nCandidate Language: ${target.selectedLanguage?.canonicalName ?: "Uncertain"}\nGloss (EN): ${target.literalGlossEn}\nGloss (FR): ${target.literalGlossFr}\nAttestation: ${if (target.dictionaryCorroboration != null) "Verified in Open Lexicon" else "Inferred morphologically"}\nFallback Status: ${if (target.isFallbackMatch) "Active" else "Direct candidate"}"
                    } else {
                        "Open-source linguistic rule engine ready. Enter a passage to analyze tokens against the 53-language catalog."
                    }
                }
                else -> {
                    // INTERACTIVE DEEP ANALYSIS AGENT
                    val response = interactiveAgent.interact(
                        userQuery = userPrompt,
                        currentPassage = _inputText.value,
                        currentWordGlosses = _wordGlosses.value,
                        activeExclusions = _excludedLanguageIds.value
                    )

                    // Apply any exclusion modifications requested conversationally
                    if (response.modifiedExclusions != null) {
                        _excludedLanguageIds.value = response.modifiedExclusions
                        analyzePassage()
                    }

                    response.replyText
                }
            }

            dao.insertChatRecord(
                promptRecord.copy(agentResponse = reply)
            )

            _agentTyping.value = false
        }
    }

    fun saveCurrentManuscript(title: String, notes: String) {
        viewModelScope.launch {
            val record = ManuscriptRecord(
                title = title.ifBlank { "Untitled Manuscript (${System.currentTimeMillis() % 10000})" },
                originalText = _inputText.value,
                sourceDescription = notes,
                excludedLanguages = _excludedLanguageIds.value.joinToString(","),
                analysisSummary = "${_wordGlosses.value.size} tokens classified"
            )
            dao.insertManuscript(record)
        }
    }

    fun saveScholarNote(content: String, noteType: String) {
        val selected = _selectedWordGloss.value ?: return
        viewModelScope.launch {
            val note = ScholarNoteRecord(
                manuscriptId = 1,
                token = selected.rawToken,
                wordIndex = selected.index,
                noteType = noteType,
                content = content,
                authorAgent = _selectedAgent.value
            )
            dao.insertNote(note)
        }
    }

    fun deleteManuscript(id: Long) {
        viewModelScope.launch {
            dao.deleteManuscript(id)
        }
    }
}
