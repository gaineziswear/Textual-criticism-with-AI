package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SmartToy
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.data.AgentChatRecord
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MultiAgentStudioScreen(
    viewModel: GlossaViewModel,
    modifier: Modifier = Modifier
) {
    val selectedAgent by viewModel.selectedAgent.collectAsState()
    val chatRecords by viewModel.chatRecords.collectAsState()
    val isTyping by viewModel.agentTyping.collectAsState()
    val currentWordGlosses by viewModel.wordGlosses.collectAsState()
    val excludedIds by viewModel.excludedLanguageIds.collectAsState()

    var userMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatRecords.size) {
        if (chatRecords.isNotEmpty()) {
            listState.animateScrollToItem(chatRecords.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Agent Selector Header
        Text(
            text = "Multi-Agent Linguistic Studio",
            style = MaterialTheme.typography.titleLarge,
            color = ParchmentGold
        )
        Text(
            text = "Engage specialized AI agents for deep philology and Semitic manuscript collation",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Agent Switcher Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AgentTabButton(
                title = "Deep Analysis Agent",
                subtitle = "Morphology & Fallback",
                icon = Icons.Default.Psychology,
                isSelected = selectedAgent == "INTERACTIVE",
                onClick = { viewModel.setSelectedAgent("INTERACTIVE") },
                modifier = Modifier.weight(1f)
            )

            AgentTabButton(
                title = "Semitic Textual Critic",
                subtitle = "Scribal Errors & Roots",
                icon = Icons.Default.MenuBook,
                isSelected = selectedAgent == "SEMITIC_CRITIC",
                onClick = { viewModel.setSelectedAgent("SEMITIC_CRITIC") },
                modifier = Modifier.weight(1f)
            )

            AgentTabButton(
                title = "Open-Source Baseline",
                subtitle = "Deterministic Rules",
                icon = Icons.Default.Code,
                isSelected = selectedAgent == "OPEN_SOURCE",
                onClick = { viewModel.setSelectedAgent("OPEN_SOURCE") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Active Agent Scope Card
        AgentScopeBanner(agentType = selectedAgent, excludedCount = excludedIds.size)

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Suggestion Chips
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            when (selectedAgent) {
                "SEMITIC_CRITIC" -> {
                    QuickChip("Detect paleographic Dalet/Resh confusions") {
                        userMessage = "Evaluate the current passage for Dalet (ד) vs Resh (ר) or Waw (ו) vs Yod (י) paleographic hazards."
                    }
                    QuickChip("Scan for scribal dittography") {
                        userMessage = "Examine tokens for accidental scribal grapheme duplication or haplography."
                    }
                    QuickChip("Trace Semitic *š-l-m cognates") {
                        userMessage = "Trace comparative Semitic cognates and sound correspondences for word #1."
                    }
                }
                "OPEN_SOURCE" -> {
                    QuickChip("Verify Unicode script tiers") {
                        userMessage = "Verify Unicode script tier classification across all current tokens."
                    }
                    QuickChip("Check dictionary direct attestations") {
                        userMessage = "List all tokens with verified dictionary attestations in the open lexicon."
                    }
                }
                else -> {
                    QuickChip("Analyze morphology of word #1") {
                        userMessage = "Provide deep morphological breakdown of word #1."
                    }
                    QuickChip("Test root fallback chain") {
                        userMessage = "Explain the root-language fallback chain for the active passage."
                    }
                    QuickChip("Exclude Thamudic") {
                        userMessage = "Exclude Thamudic from candidate consideration."
                    }
                    QuickChip("Explain confidence ratings") {
                        userMessage = "Why were specific confidence ratings assigned to these tokens?"
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat Message Stream
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (chatRecords.isEmpty()) {
                Surface(
                    color = SurfaceCard,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SurfaceCardBorder),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = ParchmentGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Consult the ${if (selectedAgent == "SEMITIC_CRITIC") "Semitic Textual Critic" else if (selectedAgent == "OPEN_SOURCE") "Open-Source Baseline" else "Interactive Deep-Analysis Agent"}",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ask about specific words, test linguistic root fallbacks, or request scribal error analysis.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chatRecords) { chat ->
                        ChatMessageItem(chat)
                    }
                    if (isTyping) {
                        item {
                            Surface(
                                color = SurfaceCard,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = ParchmentGold,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Agent evaluating philological evidence...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userMessage,
                onValueChange = { userMessage = it },
                placeholder = { Text("Ask the agent (e.g. 'Analyze word #2', 'Exclude Aramaic')...", fontSize = 12.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("agent_chat_input"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ParchmentGold,
                    unfocusedBorderColor = SurfaceCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (userMessage.isNotBlank()) {
                        val msg = userMessage
                        userMessage = ""
                        viewModel.sendAgentMessage(msg)
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ParchmentGold)
                    .testTag("agent_send_button")
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send prompt",
                    tint = Color(0xFF0F172A),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AgentTabButton(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) SurfaceCard else Color(0xFF131B2E),
        border = BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) ParchmentGold else SurfaceCardBorder
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .testTag("agent_tab_${title.take(6)}")
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) ParchmentGold else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                ),
                color = if (isSelected) TextPrimary else TextSecondary,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = TextMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
fun AgentScopeBanner(agentType: String, excludedCount: Int) {
    val (title, desc, color) = when (agentType) {
        "SEMITIC_CRITIC" -> Triple(
            "Semitic Textual-Critic Specialist",
            "Specialized in Northwest Semitic, Arabic, Ethiopic, Cuneiform and paleographic error patterns. Strictly non-interpretive.",
            CyanGlow
        )
        "OPEN_SOURCE" -> Triple(
            "Open-Source Philological Baseline",
            "Deterministic on-device rule engine executing local Unicode catalog parsing and offline lexicon lookup.",
            HighConfidenceGreen
        )
        else -> Triple(
            "Interactive Deep-Analysis Agent",
            "Interactive researcher companion. Session-aware: handles exclusions, morphology, and fallback chains.",
            ParchmentGold
        )
    }

    Surface(
        color = Color(0xFF161F30),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = if (excludedCount > 0) "$excludedCount Excluded" else "All 53 Active",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = if (excludedCount > 0) ScribalErrorRed else LapisCyan,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = TextSecondary,
                maxLines = 2
            )
        }
    }
}

@Composable
fun QuickChip(text: String, onClick: () -> Unit) {
    Surface(
        color = SurfaceCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SurfaceCardBorder),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = LapisCyan,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun ChatMessageItem(chat: AgentChatRecord) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // User Message Bubble
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                color = ParchmentGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ParchmentGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Text(
                    text = chat.userPrompt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // Agent Response Bubble
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Surface(
                color = SurfaceCard,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SurfaceCardBorder),
                modifier = Modifier.fillMaxWidth(0.95f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (chat.agentType) {
                                "SEMITIC_CRITIC" -> "Semitic Textual Critic"
                                "OPEN_SOURCE" -> "Open-Source Baseline"
                                else -> "Deep-Analysis Agent"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ParchmentGold
                            )
                        )
                        if (chat.contextToken != null) {
                            Text(
                                text = "Token: [${chat.contextToken}]",
                                style = MaterialTheme.typography.labelSmall,
                                color = LapisCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = chat.agentResponse,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}
