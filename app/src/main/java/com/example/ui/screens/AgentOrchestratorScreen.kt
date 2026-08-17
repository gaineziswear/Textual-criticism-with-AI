package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SampleManuscripts
import com.example.model.OrchestratedDossier
import com.example.model.OrchestrationStage
import com.example.model.StageLog
import com.example.ui.components.ActiveExclusionsBar
import com.example.ui.components.ConfidenceBadge
import com.example.ui.components.ExclusionFilterDialog
import com.example.ui.components.TierBadge
import com.example.ui.components.WordCard
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.FallbackAmethyst
import com.example.ui.theme.FallbackContainer
import com.example.ui.theme.GoldenAmberLight
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.HighConfidenceGreen
import com.example.ui.theme.LapisCyan
import com.example.ui.theme.ParchmentGold
import com.example.ui.theme.ScribalErrorRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AgentOrchestratorViewModel
import com.example.viewmodel.GlossaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AgentOrchestratorScreen(
    orchestratorVm: AgentOrchestratorViewModel,
    glossaVm: GlossaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentStage by orchestratorVm.currentStage.collectAsState()
    val isOrchestrating by orchestratorVm.isOrchestrating.collectAsState()
    val isStepByStep by orchestratorVm.isStepByStepMode.collectAsState()
    val stageProgress by orchestratorVm.stageProgress.collectAsState()
    val stageLogs by orchestratorVm.stageLogs.collectAsState()
    val errorMessage by orchestratorVm.errorMessage.collectAsState()

    val wordGlosses by orchestratorVm.wordGlosses.collectAsState()
    val criticReports by orchestratorVm.textualCriticReports.collectAsState()
    val providerResults by orchestratorVm.multiProviderResults.collectAsState()
    val finalDossier by orchestratorVm.finalDossier.collectAsState()

    val currentInputText by glossaVm.inputText.collectAsState()
    val excludedLanguageIds by glossaVm.excludedLanguageIds.collectAsState()
    val openAiKey by glossaVm.openAiKey.collectAsState()

    var showExclusionDialog by remember { mutableStateOf(false) }
    var archiveTitle by remember { mutableStateOf("") }
    var archiveNotes by remember { mutableStateOf("") }
    var showArchiveDialog by remember { mutableStateOf(false) }

    if (showExclusionDialog) {
        ExclusionFilterDialog(
            excludedLanguageIds = excludedLanguageIds,
            onToggleExclusion = { glossaVm.toggleLanguageExclusion(it) },
            onClearAll = { glossaVm.clearAllExclusions() },
            onDismiss = { showExclusionDialog = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Agent Orchestrator",
                        style = MaterialTheme.typography.titleLarge,
                        color = ParchmentGold
                    )
                    Text(
                        text = "Sequential analysis: Translation Agent → Textual Critic → Multi-Provider AI",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                Surface(
                    color = CyanContainer,
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.AccountTree,
                            contentDescription = null,
                            tint = CyanGlow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sequential Pipeline",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = CyanGlow
                        )
                    }
                }
            }
        }

        // Active Manuscript Context & Presets
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Input Manuscript Context",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                        color = ParchmentGold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = currentInputText,
                        onValueChange = { glossaVm.setInputText(it) },
                        placeholder = { Text("Enter or select ancient text...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("orchestrator_passage_input"),
                        minLines = 2,
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ParchmentGold,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(SampleManuscripts.ALL_SAMPLES.take(5)) { sample ->
                            Surface(
                                color = if (currentInputText == sample.originalText) GoldenAmberLight else Color(0xFF0F172A),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, if (currentInputText == sample.originalText) ParchmentGold else SurfaceCardBorder),
                                modifier = Modifier
                                    .clickable { glossaVm.loadSample(sample) }
                            ) {
                                Text(
                                    text = sample.title.substringBefore("(").trim(),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (currentInputText == sample.originalText) Color(0xFF0F172A) else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Exclusions Bar
        item {
            ActiveExclusionsBar(
                excludedLanguageIds = excludedLanguageIds,
                onRemoveExclusion = { glossaVm.toggleLanguageExclusion(it) },
                onOpenFilterDialog = { showExclusionDialog = true }
            )
        }

        // Sequential Flow Control Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161F30)),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pipeline Orchestration Mode",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            color = ParchmentGold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isStepByStep) "Step-by-Step" else "Automated",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isStepByStep) LapisCyan else HighConfidenceGreen
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isStepByStep,
                                onCheckedChange = { orchestratorVm.setStepByStepMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = LapisCyan,
                                    checkedTrackColor = CyanContainer,
                                    uncheckedThumbColor = ParchmentGold,
                                    uncheckedTrackColor = Color(0xFF334155)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!isStepByStep) {
                            Button(
                                onClick = {
                                    orchestratorVm.startSequentialPipeline(
                                        passage = currentInputText,
                                        excludedLanguages = excludedLanguageIds,
                                        apiKey = openAiKey
                                    )
                                },
                                enabled = !isOrchestrating && currentInputText.isNotBlank(),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("run_orchestrated_pipeline_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = ParchmentGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isOrchestrating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color(0xFF0F172A),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Executing...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Run Full Pipeline", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Step-by-Step controls
                            if (currentStage == OrchestrationStage.IDLE || currentStage == OrchestrationStage.COMPLETED) {
                                Button(
                                    onClick = {
                                        orchestratorVm.startStepByStepPipeline(
                                            passage = currentInputText,
                                            excludedLanguages = excludedLanguageIds,
                                            apiKey = openAiKey
                                        )
                                        orchestratorVm.advanceNextStage()
                                    },
                                    enabled = !isOrchestrating && currentInputText.isNotBlank(),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("start_step_by_step_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = LapisCyan),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Start Stage 1", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { orchestratorVm.advanceNextStage() },
                                    enabled = !isOrchestrating,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("advance_stage_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = LapisCyan),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    if (isOrchestrating) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color(0xFF0F172A),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Executing Stage...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF0F172A))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Advance to Next Stage", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        IconButton(
                            onClick = { orchestratorVm.resetPipeline() },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SurfaceCard)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TextSecondary)
                        }
                    }

                    if (isOrchestrating || stageProgress > 0f) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { stageProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = ParchmentGold,
                            trackColor = Color(0xFF334155)
                        )
                    }
                }
            }
        }

        // Sequential Pipeline Visualizer (Stepper Timeline)
        item {
            Text(
                text = "Sequential Orchestration Stages",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PipelineStageItem(
                    stageNum = 1,
                    title = "1. Translation & Root-Fallback Agent",
                    subtitle = "Script Tier classification, dictionary lookup & root fallback traversal",
                    icon = Icons.Default.Translate,
                    isActive = currentStage == OrchestrationStage.STAGE_1_TRANSLATION_AGENT,
                    isDone = currentStage.stageNumber > 1 || currentStage == OrchestrationStage.COMPLETED,
                    artifactSummary = if (wordGlosses.isNotEmpty()) "${wordGlosses.size} tokens processed (${wordGlosses.count { it.isFallbackMatch }} fallbacks)" else null
                )

                PipelineStageItem(
                    stageNum = 2,
                    title = "2. Semitic Textual Critic Agent",
                    subtitle = "Paleographic hazards (Dalet/Resh, Waw/Yod), scribal errors & Semitic cognates",
                    icon = Icons.Default.MenuBook,
                    isActive = currentStage == OrchestrationStage.STAGE_2_TEXTUAL_CRITIC,
                    isDone = currentStage.stageNumber > 2 || currentStage == OrchestrationStage.COMPLETED,
                    artifactSummary = if (criticReports.isNotEmpty()) "${criticReports.sumOf { it.potentialErrors.size }} paleographic considerations, ${criticReports.size} tokens evaluated" else null
                )

                PipelineStageItem(
                    stageNum = 3,
                    title = "3. Multi-Provider AI Service Layer",
                    subtitle = "Cross-evaluation across Gemini 3.5 Flash, OpenAI GPT & Open-Source baseline",
                    icon = Icons.Default.Compare,
                    isActive = currentStage == OrchestrationStage.STAGE_3_MODEL_COMPARISON,
                    isDone = currentStage.stageNumber > 3 || currentStage == OrchestrationStage.COMPLETED,
                    artifactSummary = if (providerResults.isNotEmpty()) "${providerResults.size} tokens verified across 3 providers" else null
                )

                PipelineStageItem(
                    stageNum = 4,
                    title = "4. Scholarly Philological Synthesis",
                    subtitle = "Consolidated academic research dossier with consensus & transmission status",
                    icon = Icons.Default.AutoAwesome,
                    isActive = currentStage == OrchestrationStage.STAGE_4_SYNTHESIS,
                    isDone = currentStage == OrchestrationStage.COMPLETED,
                    artifactSummary = if (finalDossier != null) "Dossier Compiled (${finalDossier!!.overallScholarlyStatus.label})" else null
                )
            }
        }

        // Error message if any
        if (errorMessage != null) {
            item {
                Surface(
                    color = ErrorContainer,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, ScribalErrorRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = ScribalErrorRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }
            }
        }

        // STAGE 4: Final Collated Research Dossier
        if (finalDossier != null) {
            val dossier = finalDossier!!
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131F37)),
                    border = BorderStroke(1.5.dp, ParchmentGold),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Synthesized Academic Dossier",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ParchmentGold
                                )
                                Text(
                                    text = "Generated in ${dossier.totalExecutionTimeMs}ms via 4-stage sequential orchestration",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = LapisCyan
                                )
                            }

                            Surface(
                                color = CyanContainer,
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = dossier.overallScholarlyStatus.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CyanGlow,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Consensus Summary
                        Surface(
                            color = Color(0xFF0B1120),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = dossier.overallConsensusSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFE2E8F0),
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Action Buttons: Copy Markdown & Save to Archive
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val md = buildDossierMarkdown(dossier)
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Orchestrated Dossier", md))
                                    Toast.makeText(context, "Full dossier copied to clipboard", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = LapisCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Copy Dossier", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    orchestratorVm.saveDossierToArchive("Orchestrated Collation", "")
                                    Toast.makeText(context, "Dossier saved to Scholar Archive", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ParchmentGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Archive", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Live Intermediate Results: Stage 1 Tokens
        if (wordGlosses.isNotEmpty()) {
            item {
                Text(
                    text = "Stage 1 Artifacts: Word Tokens & Literal Glosses",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    wordGlosses.forEach { gloss ->
                        Surface(
                            color = SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, SurfaceCardBorder),
                            modifier = Modifier.width(170.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("#${gloss.index + 1}", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                                    ConfidenceBadge(confidence = gloss.confidence)
                                }
                                Text(
                                    text = gloss.rawToken,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = ParchmentGold
                                )
                                Text(
                                    text = gloss.selectedLanguage?.canonicalName ?: "Uncertain",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = TextPrimary
                                )
                                Text(
                                    text = "EN: ${gloss.literalGlossEn}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = Color(0xFFCBD5E1),
                                    maxLines = 1
                                )
                                if (gloss.isFallbackMatch) {
                                    Text(
                                        text = "Root Fallback Triggered",
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                        color = FallbackAmethyst
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Intermediate Results: Stage 2 Critic Anomalies
        if (criticReports.any { it.potentialErrors.isNotEmpty() }) {
            item {
                Text(
                    text = "Stage 2 Artifacts: Scribal & Paleographic Considerations",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = ScribalErrorRed
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    criticReports.forEach { report ->
                        report.potentialErrors.forEach { anomaly ->
                            Surface(
                                color = ErrorContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, ScribalErrorRed.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Token #${report.wordIndex + 1} [${report.token}]: ${anomaly.errorType}",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = Color(0xFFFCA5A5)
                                        )
                                        Text(
                                            text = anomaly.plausibility,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ParchmentGold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = anomaly.description,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = Color(0xFFE2E8F0)
                                    )
                                    Text(
                                        text = "Proposed reading: ${anomaly.possibleAlternativeReading}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                        color = CyanGlow
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Stage Execution Logs Section
        if (stageLogs.isNotEmpty()) {
            item {
                Text(
                    text = "Orchestrator Execution Audit Log",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    stageLogs.forEach { log ->
                        Surface(
                            color = Color(0xFF0F172A),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, SurfaceCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (log.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (log.isSuccess) HighConfidenceGreen else ScribalErrorRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = log.stage.title,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ParchmentGold
                                        )
                                        Text(
                                            text = "${log.durationMs}ms",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextMuted
                                        )
                                    }
                                    Text(
                                        text = log.message,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun PipelineStageItem(
    stageNum: Int,
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    isDone: Boolean,
    artifactSummary: String? = null
) {
    val borderColor = when {
        isActive -> ParchmentGold
        isDone -> HighConfidenceGreen
        else -> SurfaceCardBorder
    }

    val containerColor = when {
        isActive -> GoldenAmberLight.copy(alpha = 0.1f)
        isDone -> GreenContainer.copy(alpha = 0.2f)
        else -> SurfaceCard
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isActive) 1.5.dp else 1.dp, borderColor),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon Circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive -> GoldenAmberLight
                            isDone -> GreenContainer
                            else -> Color(0xFF0F172A)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isActive) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFF0F172A),
                        strokeWidth = 2.dp
                    )
                } else if (isDone) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = HighConfidenceGreen,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = if (isActive || isDone) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isActive) ParchmentGold else if (isDone) TextPrimary else TextSecondary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted
                )
                if (artifactSummary != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Artifact: $artifactSummary",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = if (isDone) HighConfidenceGreen else LapisCyan
                    )
                }
            }
        }
    }
}

private fun buildDossierMarkdown(dossier: OrchestratedDossier): String {
    return buildString {
        appendLine("# Glossa Sequential Orchestration Dossier")
        appendLine("Passage: \"${dossier.passage}\"")
        appendLine("Scholarly Status: **${dossier.overallScholarlyStatus.label}**")
        appendLine("Consensus Summary: ${dossier.overallConsensusSummary}")
        appendLine("Execution Time: ${dossier.totalExecutionTimeMs}ms")
        appendLine()
        appendLine("## 1. Word-by-Word Interlinear Classification")
        appendLine("| # | Token | Script | Language | EN Gloss | FR Gloss | Confidence | Fallback |")
        appendLine("|---|---|---|---|---|---|---|---|")
        dossier.wordGlosses.forEach { word ->
            val lang = word.selectedLanguage?.canonicalName ?: "Uncertain"
            val fallback = if (word.isFallbackMatch) "Active" else "None"
            appendLine("| ${word.index + 1} | ${word.rawToken} | ${word.detectedScript} | $lang | ${word.literalGlossEn} | ${word.literalGlossFr} | ${word.confidence.label} | $fallback |")
        }
        appendLine()
        appendLine("## 2. Semitic Textual Critic Observations")
        val anomalies = dossier.textualCriticReports.flatMap { it.potentialErrors }
        if (anomalies.isEmpty()) {
            appendLine("No significant scribal anomalies detected.")
        } else {
            anomalies.forEach { anomaly ->
                appendLine("- **${anomaly.errorType}**: ${anomaly.description} (Variant: `${anomaly.possibleAlternativeReading}`)")
            }
        }
        appendLine()
        appendLine("## 3. Multi-Provider Cross-Verification")
        dossier.multiProviderComparisons.forEach { comp ->
            appendLine("### Token [${comp.token}]")
            comp.providers.forEach { p ->
                appendLine("- **${p.providerName}** (${p.modelName}): ${p.detectedLanguage} | Gloss: \"${p.literalGlossEn}\" | ${p.confidence.label}")
            }
        }
    }
}
