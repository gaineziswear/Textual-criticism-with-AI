package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AncientLanguage
import com.example.model.LanguageCatalog
import com.example.model.ScriptTier
import com.example.ui.components.TierBadge
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.FallbackAmethyst
import com.example.ui.theme.FallbackContainer
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
fun LanguageCatalogScreen(
    viewModel: GlossaViewModel,
    modifier: Modifier = Modifier
) {
    val excludedIds by viewModel.excludedLanguageIds.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedTierFilter by remember { mutableStateOf<ScriptTier?>(null) }
    var selectedFamilyFilter by remember { mutableStateOf<String?>(null) }

    val families = remember {
        LanguageCatalog.ALL_53_LANGUAGES.map { it.familyBranch }.distinct()
    }

    val filteredLanguages = remember(searchQuery, selectedTierFilter, selectedFamilyFilter) {
        LanguageCatalog.ALL_53_LANGUAGES.filter { lang ->
            val matchSearch = searchQuery.isBlank() ||
                lang.canonicalName.contains(searchQuery, ignoreCase = true) ||
                lang.id.contains(searchQuery, ignoreCase = true) ||
                lang.familyBranch.contains(searchQuery, ignoreCase = true) ||
                lang.scriptName.contains(searchQuery, ignoreCase = true)

            val matchTier = selectedTierFilter == null || lang.scriptTier == selectedTierFilter
            val matchFamily = selectedFamilyFilter == null || lang.familyBranch == selectedFamilyFilter

            matchSearch && matchTier && matchFamily
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = "53 Canonical Languages",
                        style = MaterialTheme.typography.titleLarge,
                        color = ParchmentGold
                    )
                    Text(
                        text = "Script tiers, genealogical branches & attested fallback chains",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }

                if (excludedIds.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAllExclusions() }) {
                        Text("Restore All", color = LapisCyan, fontSize = 12.sp)
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, script, or family...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_field"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ParchmentGold,
                    unfocusedBorderColor = SurfaceCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                singleLine = true
            )
        }

        // Tier Filter Chips
        item {
            Column {
                Text(
                    text = "Filter by Script Tier",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChipCustom(
                            label = "All Tiers",
                            isSelected = selectedTierFilter == null,
                            onClick = { selectedTierFilter = null }
                        )
                    }
                    items(ScriptTier.entries) { tier ->
                        FilterChipCustom(
                            label = "Tier ${tier.tierNumber}",
                            isSelected = selectedTierFilter == tier,
                            onClick = { selectedTierFilter = if (selectedTierFilter == tier) null else tier }
                        )
                    }
                }
            }
        }

        // Language List Counter
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Showing ${filteredLanguages.size} of 53 languages",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
                Text(
                    text = "${excludedIds.size} excluded from session",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (excludedIds.isNotEmpty()) ScribalErrorRed else TextMuted
                )
            }
        }

        // Languages List
        items(filteredLanguages) { lang ->
            val isExcluded = lang.id in excludedIds
            CatalogLanguageCard(
                language = lang,
                isExcluded = isExcluded,
                onToggleExclusion = { viewModel.toggleLanguageExclusion(lang.id) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun FilterChipCustom(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) GoldenAmberLight else SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSelected) ParchmentGold else SurfaceCardBorder),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) Color(0xFF0F172A) else TextSecondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CatalogLanguageCard(
    language: AncientLanguage,
    isExcluded: Boolean,
    onToggleExclusion: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isExcluded) ErrorContainer.copy(alpha = 0.3f) else SurfaceCard
        ),
        border = BorderStroke(
            1.dp,
            if (isExcluded) ScribalErrorRed.copy(alpha = 0.6f) else SurfaceCardBorder
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("catalog_card_${language.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = language.canonicalName,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isExcluded) Color(0xFFFCA5A5) else ParchmentGold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TierBadge(tier = language.scriptTier)
                }

                Surface(
                    color = if (isExcluded) ScribalErrorRed else Color(0xFF1E3A8A),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable(onClick = onToggleExclusion)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isExcluded) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isExcluded) "EXCLUDED" else "ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Branch: ${language.familyBranch} • Script: ${language.scriptName}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            if (language.fallbackLanguageIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Attested Fallback Chain: ",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = LapisCyan
                    )
                    Text(
                        text = language.fallbackLanguageIds.joinToString(" → "),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                        color = CyanGlow
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Independent Branch / Catalog Isolate (No speculative fallbacks)",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = TextMuted
                )
            }
        }
    }
}
