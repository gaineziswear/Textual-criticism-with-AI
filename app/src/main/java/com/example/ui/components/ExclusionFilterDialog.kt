package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AncientLanguage
import com.example.model.LanguageCatalog
import com.example.ui.theme.CyanContainer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ErrorContainer
import com.example.ui.theme.LapisCyan
import com.example.ui.theme.ParchmentGold
import com.example.ui.theme.ScribalErrorRed
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExclusionFilterDialog(
    excludedLanguageIds: Set<String>,
    onToggleExclusion: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            LanguageCatalog.ALL_53_LANGUAGES
        } else {
            LanguageCatalog.ALL_53_LANGUAGES.filter {
                it.canonicalName.contains(searchQuery, ignoreCase = true) ||
                it.familyBranch.contains(searchQuery, ignoreCase = true) ||
                it.scriptName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp)),
            color = SurfaceDark,
            border = BorderStroke(1.dp, SurfaceCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxHeight()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Language Exclusion Manager",
                            style = MaterialTheme.typography.titleLarge,
                            color = ParchmentGold
                        )
                        Text(
                            text = "Excluded languages are removed before model dispatch",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search 53 languages (e.g. Thamudic, Syriac, Cuneiform)...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exclusion_search_field"),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Active Exclusion Count & Quick Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Currently Excluded: ${excludedLanguageIds.size} / 53",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (excludedLanguageIds.isNotEmpty()) ScribalErrorRed else TextSecondary
                    )

                    if (excludedLanguageIds.isNotEmpty()) {
                        TextButton(onClick = onClearAll) {
                            Text("Restore All", color = LapisCyan, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Language List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredLanguages) { lang ->
                        val isExcluded = lang.id in excludedLanguageIds
                        LanguageExclusionRow(
                            language = lang,
                            isExcluded = isExcluded,
                            onToggle = { onToggleExclusion(lang.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("apply_exclusions_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ParchmentGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Apply Filter & Re-Analyze", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LanguageExclusionRow(
    language: AncientLanguage,
    isExcluded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .testTag("language_row_${language.id}"),
        colors = CardDefaults.cardColors(
            containerColor = if (isExcluded) ErrorContainer.copy(alpha = 0.4f) else SurfaceCard
        ),
        border = BorderStroke(
            1.dp,
            if (isExcluded) ScribalErrorRed.copy(alpha = 0.6f) else SurfaceCardBorder
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = language.canonicalName,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = if (isExcluded) Color(0xFFFCA5A5) else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    TierBadge(tier = language.scriptTier)
                }
                Text(
                    text = "${language.familyBranch} • ${language.scriptName}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TextMuted
                )
                if (language.fallbackLanguageIds.isNotEmpty()) {
                    Text(
                        text = "Fallback: ${language.fallbackLanguageIds.joinToString(", ")}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = CyanGlow
                    )
                }
            }

            Surface(
                color = if (isExcluded) ScribalErrorRed else Color(0xFF334155),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (isExcluded) "EXCLUDED" else "ACTIVE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isExcluded) Color.White else TextSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
        }
    }
}
