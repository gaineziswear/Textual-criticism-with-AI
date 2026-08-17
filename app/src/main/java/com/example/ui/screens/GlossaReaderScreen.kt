package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.SampleManuscripts
import com.example.ui.components.ActiveExclusionsBar
import com.example.ui.components.ExclusionFilterDialog
import com.example.ui.components.WordCard
import com.example.ui.components.WordDetailSheet
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.FallbackAmethyst
import com.example.ui.theme.GoldenAmberLight
import com.example.ui.theme.LapisCyan
import com.example.ui.theme.ParchmentGold
import com.example.ui.theme.ScribalErrorRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GlossaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlossaReaderScreen(
    viewModel: GlossaViewModel,
    onNavigateToAgent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val inputText by viewModel.inputText.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val wordGlosses by viewModel.wordGlosses.collectAsState()
    val excludedIds by viewModel.excludedLanguageIds.collectAsState()
    val selectedWord by viewModel.selectedWordGloss.collectAsState()
    val textualCriticReport by viewModel.textualCriticReport.collectAsState()
    val multiProviderResult by viewModel.multiProviderResult.collectAsState()
    val isComparing by viewModel.isComparingProviders.collectAsState()

    var showExclusionDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var saveTitle by remember { mutableStateOf("") }
    var saveNotes by remember { mutableStateOf("") }

    if (showExclusionDialog) {
        ExclusionFilterDialog(
            excludedLanguageIds = excludedIds,
            onToggleExclusion = { viewModel.toggleLanguageExclusion(it) },
            onClearAll = { viewModel.clearAllExclusions() },
            onDismiss = { showExclusionDialog = false }
        )
    }

    if (selectedWord != null) {
        WordDetailSheet(
            gloss = selectedWord!!,
            textualCriticReport = textualCriticReport,
            multiProviderResult = multiProviderResult,
            isComparing = isComparing,
            onRunComparison = { viewModel.runMultiProviderComparison(selectedWord!!) },
            onSaveNote = { content, type -> viewModel.saveScholarNote(content, type) },
            onDismiss = { viewModel.selectWordForDetail(null) }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Banner & Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = R.drawable.glossa_hero_banner_1786929775658,
                        contentDescription = "Glossa Header Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                            .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GLOSSA",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    letterSpacing = 2.sp,
                                    fontSize = 24.sp
                                ),
                                color = ParchmentGold
                            )
                            Text(
                                text = "Ancient Manuscript Word-by-Word Analyzer",
                                style = MaterialTheme.typography.bodySmall,
                                color = LapisCyan
                            )
                        }

                        Surface(
                            color = CyanContainer,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, CyanGlow.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "53 Canonical Languages",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = CyanGlow,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // Preloaded Historical Samples Picker
        item {
            Column {
                Text(
                    text = "Historical Epigraphic Samples",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SampleManuscripts.ALL_SAMPLES) { sample ->
                        Surface(
                            color = if (inputText == sample.originalText) GoldenAmberLight else SurfaceCard,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (inputText == sample.originalText) ParchmentGold else SurfaceCardBorder
                            ),
                            modifier = Modifier
                                .testTag("sample_chip_${sample.title.take(6)}")
                                .clickable { viewModel.loadSample(sample) }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(
                                    text = sample.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                                    color = if (inputText == sample.originalText) Color(0xFF0F172A) else ParchmentGold,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = sample.tradition,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                    color = if (inputText == sample.originalText) Color(0xFF334155) else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Exclusions Status Bar
        item {
            ActiveExclusionsBar(
                excludedLanguageIds = excludedIds,
                onRemoveExclusion = { viewModel.toggleLanguageExclusion(it) },
                onOpenFilterDialog = { showExclusionDialog = true }
            )
        }

        // Manuscript Input Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Manuscript Passage / Epigraph Text",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = ParchmentGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { viewModel.setInputText(it) },
                        placeholder = { Text("Paste ancient text in Hebrew, Syriac, Cuneiform, Greek, Musnad, etc...", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manuscript_input_field"),
                        minLines = 2,
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ParchmentGold,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.analyzePassage() },
                                colors = ButtonDefaults.buttonColors(containerColor = ParchmentGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("analyze_button")
                            ) {
                                Icon(Icons.Default.Translate, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Analyze", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { showExclusionDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, SurfaceCardBorder),
                                modifier = Modifier.testTag("filter_exclusions_button")
                            ) {
                                Icon(Icons.Default.FilterList, contentDescription = null, tint = LapisCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Exclusions (${excludedIds.size})", color = LapisCyan, fontSize = 12.sp)
                            }
                        }

                        IconButton(onClick = { viewModel.setInputText("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear input", tint = TextSecondary)
                        }
                    }
                }
            }
        }

        // Analysis Summary & Token Count Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Word-by-Word Interlinear Analysis",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ParchmentGold,
                            strokeWidth = 2.dp
                        )
                    }
                }

                Text(
                    text = "${wordGlosses.size} tokens",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }
        }

        // Interlinear Tokens Grid (FlowRow)
        item {
            if (wordGlosses.isEmpty()) {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No tokens parsed. Enter a manuscript passage or select an epigraphic sample above to begin literal glossing.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    }
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    wordGlosses.forEach { gloss ->
                        WordCard(
                            gloss = gloss,
                            isSelected = selectedWord?.index == gloss.index,
                            onClick = { viewModel.selectWordForDetail(gloss) },
                            modifier = Modifier.width(170.dp)
                        )
                    }
                }
            }
        }

        // Bottom Spacing for Navigation Safe Area
        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
