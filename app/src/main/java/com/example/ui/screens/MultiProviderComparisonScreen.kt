package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MultiProviderComparisonResult
import com.example.model.ProviderComparisonItem
import com.example.model.WordGloss
import com.example.ui.components.ConfidenceBadge
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
import com.example.viewmodel.GlossaViewModel

@Composable
fun MultiProviderComparisonScreen(
    viewModel: GlossaViewModel,
    modifier: Modifier = Modifier
) {
    val wordGlosses by viewModel.wordGlosses.collectAsState()
    val selectedWord by viewModel.selectedWordGloss.collectAsState()
    val multiProviderResult by viewModel.multiProviderResult.collectAsState()
    val isComparing by viewModel.isComparingProviders.collectAsState()
    val openAiKey by viewModel.openAiKey.collectAsState()

    var showKeyConfig by remember { mutableStateOf(false) }
    var keyInput by remember { mutableStateOf(openAiKey) }
    var hideKeyText by remember { mutableStateOf(true) }

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
                        text = "Multi-Provider AI Comparison",
                        style = MaterialTheme.typography.titleLarge,
                        color = ParchmentGold
                    )
                    Text(
                        text = "Side-by-side analysis across Gemini, GPT, and Open-Source baseline",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                IconButton(
                    onClick = { showKeyConfig = !showKeyConfig },
                    modifier = Modifier.testTag("toggle_key_config_button")
                ) {
                    Icon(
                        Icons.Default.Key,
                        contentDescription = "Configure provider keys",
                        tint = if (openAiKey.isNotBlank()) HighConfidenceGreen else LapisCyan
                    )
                }
            }
        }

        // Security Notice Banner (§8)
        item {
            Surface(
                color = Color(0xFF161F30),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SurfaceCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = LapisCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Security Mandate: Gemini API uses BuildConfig injection. Additional provider keys are stored in secure local memory and never logged or persisted publicly.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = TextSecondary
                    )
                }
            }
        }

        // Optional OpenAI Key Configuration Card
        if (showKeyConfig) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, ParchmentGold.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Configure Secondary Provider (OpenAI GPT)",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = ParchmentGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter optional OpenAI API key for live GPT comparison. Otherwise, academic reference baseline is used.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = keyInput,
                            onValueChange = { keyInput = it },
                            placeholder = { Text("sk-...", fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("openai_key_input"),
                            visualTransformation = if (hideKeyText) PasswordVisualTransformation() else VisualTransformation.None,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ParchmentGold,
                                unfocusedBorderColor = SurfaceCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    viewModel.setOpenAiKey(keyInput.trim())
                                    showKeyConfig = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ParchmentGold),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Save Key", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Select Token to Compare
        item {
            Column {
                Text(
                    text = "Select Word Token for Comparative Run",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (wordGlosses.isEmpty()) {
                    Text(
                        text = "Enter a passage in the Reader tab first to generate word tokens.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(wordGlosses) { gloss ->
                            val isSelected = selectedWord?.index == gloss.index
                            Surface(
                                color = if (isSelected) ParchmentGold else SurfaceCard,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, if (isSelected) ParchmentGold else SurfaceCardBorder),
                                modifier = Modifier
                                    .testTag("compare_token_chip_${gloss.index}")
                                    .clickable {
                                        viewModel.selectWordForDetail(gloss)
                                        viewModel.runMultiProviderComparison(gloss)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "#${gloss.index + 1} ${gloss.rawToken}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                                        color = if (isSelected) Color(0xFF0F172A) else TextPrimary,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Trigger Comparison Button
        if (selectedWord != null) {
            item {
                Button(
                    onClick = { viewModel.runMultiProviderComparison(selectedWord!!) },
                    enabled = !isComparing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("execute_comparison_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ParchmentGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (isComparing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color(0xFF0F172A),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Querying Providers...", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Compare, contentDescription = null, tint = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Compare Providers for [${selectedWord!!.rawToken}]",
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Comparison Result Table & Cards
        if (multiProviderResult != null) {
            val result = multiProviderResult!!

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF161F30)),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Agreement Matrix & Divergence",
                            style = MaterialTheme.typography.labelSmall,
                            color = LapisCyan
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = result.agreementSummary,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            color = ParchmentGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            items(result.providers) { provider ->
                ProviderResultCard(provider = provider)
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun ProviderResultCard(provider: ProviderComparisonItem) {
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
                Column {
                    Text(
                        text = provider.providerName,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = ParchmentGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Model: ${provider.modelName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }
                ConfidenceBadge(confidence = provider.confidence)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color(0xFF0B1120),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "Classified Language: ${provider.detectedLanguage}",
                        style = MaterialTheme.typography.titleSmall,
                        color = CyanGlow
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Literal Gloss (EN): \"${provider.literalGlossEn}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                    Text(
                        text = "Glose Littérale (FR): \"${provider.literalGlossFr}\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Morphology: ${provider.grammaticalAnalysis}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondary
            )

            if (provider.isFallbackUsed) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Fallback chain was traversed to resolve root lexeme",
                    style = MaterialTheme.typography.labelSmall,
                    color = FallbackAmethyst
                )
            }
        }
    }
}
