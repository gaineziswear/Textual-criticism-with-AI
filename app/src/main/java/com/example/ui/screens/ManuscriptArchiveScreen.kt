package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ManuscriptRecord
import com.example.ui.theme.CyanGlow
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ManuscriptArchiveScreen(
    viewModel: GlossaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedManuscripts by viewModel.savedManuscripts.collectAsState()
    val currentInput by viewModel.inputText.collectAsState()
    val currentWordGlosses by viewModel.wordGlosses.collectAsState()

    var saveTitle by remember { mutableStateOf("") }
    var saveNotes by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Scholar Archive & Notes",
                style = MaterialTheme.typography.titleLarge,
                color = ParchmentGold
            )
            Text(
                text = "Locally persisted manuscript records, collations & paleographic notes",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        // Save Current Passage Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Archive Current Manuscript Session",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                        color = ParchmentGold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = saveTitle,
                        onValueChange = { saveTitle = it },
                        placeholder = { Text("Manuscript Title / Siglum (e.g. 4QSam-a, Mesha-Stele)...", fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("archive_title_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ParchmentGold,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = saveNotes,
                        onValueChange = { saveNotes = it },
                        placeholder = { Text("Context notes, provenience, or codex details...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ParchmentGold,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current: ${currentWordGlosses.size} tokens parsed",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )

                        Button(
                            onClick = {
                                viewModel.saveCurrentManuscript(saveTitle, saveNotes)
                                saveTitle = ""
                                saveNotes = ""
                                Toast.makeText(context, "Manuscript saved to archive", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ParchmentGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_archive_button")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save to Archive", color = Color(0xFF0F172A), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Export Entire Dossier as Markdown
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161F30)),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Export Interlinear Markdown Report",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                            color = CyanGlow
                        )
                        Text(
                            text = "Copy academic report of all analyzed tokens, glosses, and root fallbacks.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = TextMuted
                        )
                    }

                    Button(
                        onClick = {
                            val report = buildMarkdownReport(currentInput, currentWordGlosses)
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Glossa Report", report))
                            Toast.makeText(context, "Report copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LapisCyan),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF0F172A), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Report", color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Saved Records Header
        item {
            Text(
                text = "Saved Manuscript Dossiers (${savedManuscripts.size})",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }

        if (savedManuscripts.isEmpty()) {
            item {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No saved manuscripts yet. Use the form above to archive your current linguistic session.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            }
        } else {
            items(savedManuscripts) { manuscript ->
                SavedManuscriptCard(
                    record = manuscript,
                    formattedDate = dateFormat.format(Date(manuscript.timestamp)),
                    onLoad = {
                        viewModel.setInputText(manuscript.originalText)
                        viewModel.analyzePassage()
                        Toast.makeText(context, "Loaded \"${manuscript.title}\" into Reader", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = {
                        viewModel.deleteManuscript(manuscript.id)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun SavedManuscriptCard(
    record: ManuscriptRecord,
    formattedDate: String,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = ParchmentGold,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = record.originalText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCBD5E1),
                    maxLines = 2,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (record.sourceDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Notes: ${record.sourceDescription}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onLoad,
                    colors = ButtonDefaults.buttonColors(containerColor = LapisCyan),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Load into Reader", color = Color(0xFF0F172A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete record", tint = ScribalErrorRed)
                }
            }
        }
    }
}

private fun buildMarkdownReport(passage: String, wordGlosses: List<com.example.model.WordGloss>): String {
    return buildString {
        appendLine("# Glossa Philological Interlinear Report")
        appendLine("Passage: $passage")
        appendLine()
        appendLine("| # | Token | Script | Language | EN Gloss | FR Gloss | Confidence | Lexicon |")
        appendLine("|---|---|---|---|---|---|---|---|")
        wordGlosses.forEach { word ->
            val dict = word.dictionaryCorroboration?.source ?: "Inferred"
            val fallback = if (word.isFallbackMatch) " (Fallback)" else ""
            val lang = (word.selectedLanguage?.canonicalName ?: "Uncertain") + fallback
            appendLine("| ${word.index + 1} | ${word.rawToken} | ${word.detectedScript} | $lang | ${word.literalGlossEn} | ${word.literalGlossFr} | ${word.confidence.label} | $dict |")
        }
        appendLine()
        appendLine("*Generated by Glossa Multilingual Ancient Manuscript Research Engine.*")
    }
}
