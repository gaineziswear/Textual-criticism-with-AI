package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MultiProviderComparisonResult
import com.example.model.TextualCriticReport
import com.example.model.WordGloss
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.FallbackAmethyst
import com.example.ui.theme.FallbackContainer
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun WordDetailSheet(
    gloss: WordGloss,
    textualCriticReport: TextualCriticReport?,
    multiProviderResult: MultiProviderComparisonResult?,
    isComparing: Boolean,
    onRunComparison: () -> Unit,
    onSaveNote: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var noteContent by remember { mutableStateOf("") }
    var showAddNote by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceDark,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Philological & Text-Critical Dossier",
                        style = MaterialTheme.typography.labelSmall,
                        color = LapisCyan
                    )
                    Text(
                        text = "Word #${gloss.index + 1}: ${gloss.rawToken}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = ParchmentGold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            if (!gloss.transliteration.isNullOrBlank()) {
                Text(
                    text = "Transliteration: [${gloss.transliteration}]",
                    style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    color = CyanGlow
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Identification Summary Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
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
                            text = gloss.selectedLanguage?.canonicalName ?: "Uncertain / Unclassified",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        ConfidenceBadge(confidence = gloss.confidence)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Script: ${gloss.detectedScript} • ${gloss.scriptTier.description}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )

                    if (gloss.selectedLanguage != null) {
                        Text(
                            text = "Family: ${gloss.selectedLanguage.familyBranch}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Literal Gloss Box
                    Surface(
                        color = Color(0xFF0B1120),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Literal English Gloss:",
                                style = MaterialTheme.typography.labelSmall,
                                color = ParchmentGold
                            )
                            Text(
                                text = gloss.literalGlossEn,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                                color = Color.White
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Glose Littérale Française:",
                                style = MaterialTheme.typography.labelSmall,
                                color = LapisCyan
                            )
                            Text(
                                text = gloss.literalGlossFr,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    // Root Fallback Details if Active
                    if (gloss.isFallbackMatch && gloss.fallbackDetails != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = FallbackContainer,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, FallbackAmethyst.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = FallbackAmethyst,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Root-Language Fallback Triggered",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                        color = FallbackAmethyst,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = gloss.fallbackDetails,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFE9D5FF)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dictionary & Lexicon Corroboration Section
            Text(
                text = "Lexical Corroboration",
                style = MaterialTheme.typography.titleMedium,
                color = ParchmentGold
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (gloss.dictionaryCorroboration != null) {
                val dict = gloss.dictionaryCorroboration
                Card(
                    colors = CardDefaults.cardColors(containerColor = GreenContainer.copy(alpha = 0.3f)),
                    border = BorderStroke(1.dp, HighConfidenceGreen.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Source: ${dict.source}",
                                style = MaterialTheme.typography.labelSmall,
                                color = HighConfidenceGreen,
                                fontWeight = FontWeight.Bold
                            )
                            if (dict.isDirectAttestation) {
                                Text(
                                    text = "Direct Lexicon Attestation",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyanGlow
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Lemma: ${dict.lemma} ${dict.root?.let { " [Root: $it]" } ?: ""}",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            color = TextPrimary
                        )

                        if (!dict.partOfSpeech.isNullOrBlank()) {
                            Text(
                                text = "Grammar: ${dict.partOfSpeech}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Definition: ${dict.definitionEn}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }
            } else {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No direct dictionary entry in primary classical lexicon. Morphological parsing inferred based on canonical grammar rules.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Semitic Textual Critic Section
            if (textualCriticReport != null) {
                Text(
                    text = "Semitic Textual Criticism",
                    style = MaterialTheme.typography.titleMedium,
                    color = ParchmentGold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Transmission Status:",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Surface(
                                color = CyanContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = textualCriticReport.transmissionStatus.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CyanGlow,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Anomalies / Hazards
                        if (textualCriticReport.potentialErrors.isNotEmpty()) {
                            Text(
                                text = "Paleographic & Scribal Considerations:",
                                style = MaterialTheme.typography.labelSmall,
                                color = ScribalErrorRed,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            textualCriticReport.potentialErrors.forEach { anomaly ->
                                Surface(
                                    color = ErrorContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "• ${anomaly.errorType} (${anomaly.plausibility})",
                                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                                            color = Color(0xFFFCA5A5)
                                        )
                                        Text(
                                            text = anomaly.description,
                                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                            color = Color(0xFFE2E8F0)
                                        )
                                        Text(
                                            text = "Variant reading: ${anomaly.possibleAlternativeReading}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                                            color = ParchmentGold
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "No characteristic scribal corruptions (haplography, dittography, homoioteleuton) identified in isolated reading.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }

                        // Comparative Cognates
                        if (textualCriticReport.comparativeRoots.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Comparative Semitic Cognates:",
                                style = MaterialTheme.typography.labelSmall,
                                color = LapisCyan,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                textualCriticReport.comparativeRoots.forEach { cognate ->
                                    Surface(
                                        color = Color(0xFF0F172A),
                                        shape = RoundedCornerShape(6.dp),
                                        border = BorderStroke(1.dp, SurfaceCardBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(
                                                text = cognate.language,
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                                color = TextMuted
                                            )
                                            Text(
                                                text = cognate.form,
                                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                color = ParchmentGold
                                            )
                                            Text(
                                                text = cognate.meaning,
                                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multi-Provider Comparison Trigger & Results
            Text(
                text = "Multi-Provider AI Comparison",
                style = MaterialTheme.typography.titleMedium,
                color = ParchmentGold
            )
            Spacer(modifier = Modifier.height(6.dp))

            if (multiProviderResult != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161F30)),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Agreement Matrix: ${multiProviderResult.agreementSummary}",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = CyanGlow,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        multiProviderResult.providers.forEach { provider ->
                            Surface(
                                color = SurfaceCard,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${provider.providerName} (${provider.modelName})",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = ParchmentGold
                                        )
                                        ConfidenceBadge(confidence = provider.confidence)
                                    }
                                    Text(
                                        text = "Language: ${provider.detectedLanguage}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "Gloss: \"${provider.literalGlossEn}\"",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Morphology: ${provider.grammaticalAnalysis}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Button(
                    onClick = onRunComparison,
                    enabled = !isComparing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("run_provider_comparison_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = LapisCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Compare, contentDescription = null, tint = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isComparing) "Comparing Gemini, GPT & Open-Source..." else "Compare Models Side-by-Side",
                        color = Color(0xFF0F172A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scholar Note Creator
            if (!showAddNote) {
                OutlinedButton(
                    onClick = { showAddNote = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = ParchmentGold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Scholar Note to Archive", color = ParchmentGold)
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, ParchmentGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Record Epigraphic Note for [${gloss.rawToken}]",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = ParchmentGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = noteContent,
                            onValueChange = { noteContent = it },
                            placeholder = { Text("Enter paleographical, dialectal, or collation notes...", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showAddNote = false }) {
                                Text("Cancel", color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (noteContent.isNotBlank()) {
                                        onSaveNote(noteContent, "Paleographical Note")
                                        noteContent = ""
                                        showAddNote = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ParchmentGold)
                            ) {
                                Text("Save Note", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
