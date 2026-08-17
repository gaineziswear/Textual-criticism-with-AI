package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Confidence
import com.example.model.LanguageCatalog
import com.example.model.ScriptTier
import com.example.model.WordGloss
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WordCard(
    gloss: WordGloss,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) ParchmentGold else SurfaceCardBorder
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = modifier
            .testTag("word_card_${gloss.index}")
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1E293B) else SurfaceCard
        ),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header: Token Index & Script Tier Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${gloss.index + 1}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )

                TierBadge(tier = gloss.scriptTier)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Ancient Script Word Display
            Text(
                text = gloss.rawToken,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = ParchmentGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!gloss.transliteration.isNullOrBlank()) {
                Text(
                    text = gloss.transliteration,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    ),
                    color = LapisCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Detected Language & Confidence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val langName = gloss.selectedLanguage?.canonicalName ?: "Uncertain"
                Text(
                    text = langName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Spacer(modifier = Modifier.width(6.dp))
                ConfidenceBadge(confidence = gloss.confidence)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Literal Glosses (EN + FR)
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(6.dp)) {
                    Text(
                        text = "EN: ${gloss.literalGlossEn}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = Color(0xFFE2E8F0),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "FR: ${gloss.literalGlossFr}",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF94A3B8),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Fallback indicator or Dictionary Corroboration Tag
            if (gloss.isFallbackMatch) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = FallbackContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Fallback match",
                            tint = FallbackAmethyst,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Root Fallback (Cap: Low)",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = FallbackAmethyst
                        )
                    }
                }
            } else if (gloss.dictionaryCorroboration != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = GreenContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Book,
                            contentDescription = "Dictionary hit",
                            tint = HighConfidenceGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lexicon: ${gloss.dictionaryCorroboration.source}",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = HighConfidenceGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TierBadge(tier: ScriptTier) {
    val (bgColor, textColor, label) = when (tier) {
        ScriptTier.TIER_1 -> Triple(CyanContainer, CyanGlow, "Tier 1")
        ScriptTier.TIER_2 -> Triple(Color(0xFF1E3A8A), Color(0xFF93C5FD), "Tier 2")
        ScriptTier.TIER_3 -> Triple(Color(0xFF334155), Color(0xFFCBD5E1), "Tier 3")
        ScriptTier.TIER_4 -> Triple(ErrorContainer, ScribalErrorRed, "Tier 4")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ConfidenceBadge(confidence: Confidence) {
    val (bgColor, textColor) = when (confidence) {
        Confidence.HIGH -> Pair(GreenContainer, HighConfidenceGreen)
        Confidence.MEDIUM -> Pair(CyanContainer, CyanGlow)
        Confidence.LOW -> Pair(FallbackContainer, FallbackAmethyst)
        Confidence.UNCERTAIN -> Pair(Color(0xFF334155), TextMuted)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = confidence.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            ),
            color = textColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveExclusionsBar(
    excludedLanguageIds: Set<String>,
    onRemoveExclusion: (String) -> Unit,
    onOpenFilterDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFF161F30),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SurfaceCardBorder)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Language exclusions",
                        tint = if (excludedLanguageIds.isNotEmpty()) ScribalErrorRed else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (excludedLanguageIds.isEmpty()) "Candidate Exclusions: None (All 53 Active)" else "Excluded Candidates (${excludedLanguageIds.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 12.sp),
                        color = if (excludedLanguageIds.isNotEmpty()) ParchmentGold else TextSecondary
                    )
                }

                Text(
                    text = "Manage",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = LapisCyan,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .testTag("manage_exclusions_button")
                        .clickable(onClick = onOpenFilterDialog)
                        .padding(4.dp)
                )
            }

            if (excludedLanguageIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    excludedLanguageIds.forEach { id ->
                        val lang = LanguageCatalog.getById(id)
                        val name = lang?.canonicalName ?: id
                        Surface(
                            color = ErrorContainer,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, ScribalErrorRed.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = Color(0xFFFCA5A5)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = { onRemoveExclusion(id) },
                                    modifier = Modifier.size(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove exclusion",
                                        tint = ScribalErrorRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
