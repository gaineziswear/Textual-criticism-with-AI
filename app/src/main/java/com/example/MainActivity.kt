package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Compare
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.AgentOrchestratorScreen
import com.example.ui.screens.GlossaReaderScreen
import com.example.ui.screens.LanguageCatalogScreen
import com.example.ui.screens.ManuscriptArchiveScreen
import com.example.ui.screens.MultiAgentStudioScreen
import com.example.ui.screens.MultiProviderComparisonScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ParchmentGold
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.viewmodel.AgentOrchestratorViewModel
import com.example.viewmodel.GlossaViewModel

enum class GlossaNavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    READER("Reader", Icons.Filled.Translate, Icons.Outlined.Translate, "nav_reader_tab"),
    ORCHESTRATOR("Pipeline", Icons.Filled.AccountTree, Icons.Outlined.AccountTree, "nav_orchestrator_tab"),
    AGENTS("Agents", Icons.Filled.Psychology, Icons.Outlined.Psychology, "nav_agents_tab"),
    COMPARE("Compare", Icons.Filled.Compare, Icons.Outlined.Compare, "nav_compare_tab"),
    CATALOG("53 Langs", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_catalog_tab"),
    ARCHIVE("Archive", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder, "nav_archive_tab")
}

class MainActivity : ComponentActivity() {

    private val glossaViewModel: GlossaViewModel by viewModels()
    private val orchestratorViewModel: AgentOrchestratorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                GlossaApp(
                    glossaVm = glossaViewModel,
                    orchestratorVm = orchestratorViewModel
                )
            }
        }
    }
}

@Composable
fun GlossaApp(
    glossaVm: GlossaViewModel,
    orchestratorVm: AgentOrchestratorViewModel
) {
    var selectedTabPosition by rememberSaveable { mutableIntStateOf(0) }
    val tabs = GlossaNavTab.entries

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTabPosition == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabPosition = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp
                                ),
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0F172A),
                            selectedTextColor = ParchmentGold,
                            indicatorColor = ParchmentGold,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (tabs[selectedTabPosition]) {
                GlossaNavTab.READER -> GlossaReaderScreen(
                    viewModel = glossaVm,
                    onNavigateToAgent = { selectedTabPosition = 1 }
                )
                GlossaNavTab.ORCHESTRATOR -> AgentOrchestratorScreen(
                    orchestratorVm = orchestratorVm,
                    glossaVm = glossaVm
                )
                GlossaNavTab.AGENTS -> MultiAgentStudioScreen(
                    viewModel = glossaVm
                )
                GlossaNavTab.COMPARE -> MultiProviderComparisonScreen(
                    viewModel = glossaVm
                )
                GlossaNavTab.CATALOG -> LanguageCatalogScreen(
                    viewModel = glossaVm
                )
                GlossaNavTab.ARCHIVE -> ManuscriptArchiveScreen(
                    viewModel = glossaVm
                )
            }
        }
    }
}
