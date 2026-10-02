package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.WinterArcDatabase
import com.example.data.repository.WinterArcRepository
import com.example.ui.components.WinterArcBottomNav
import com.example.ui.screens.ArcScreen
import com.example.ui.screens.BackupScreen
import com.example.ui.screens.ReviewScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.ObsidianVoid
import com.example.ui.theme.WinterArcTheme
import com.example.viewmodel.ArcTab
import com.example.viewmodel.WinterArcViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WinterArcViewModel by viewModels {
        val database = WinterArcDatabase.getDatabase(applicationContext)
        val repository = WinterArcRepository(database.winterArcDao())
        WinterArcViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.example.notification.TaskNotificationHelper.createNotificationChannel(applicationContext)

        if (intent?.hasExtra("EXTRA_TASK_ID") == true) {
            viewModel.selectTab(ArcTab.TODAY)
        }

        setContent {
            WinterArcTheme {
                WinterArcApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WinterArcApp(viewModel: WinterArcViewModel) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    // Handle back button: return to TODAY tab if on any other tab
    BackHandler(enabled = currentTab != ArcTab.TODAY) {
        viewModel.selectTab(ArcTab.TODAY)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianVoid),
        containerColor = ObsidianVoid,
        bottomBar = {
            WinterArcBottomNav(
                currentTab = currentTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            Crossfade(
                targetState = currentTab,
                animationSpec = tween(durationMillis = 200),
                label = "screen_crossfade"
            ) { tab ->
                when (tab) {
                    ArcTab.TODAY -> TodayScreen(
                        viewModel = viewModel,
                        onNavigateToArc = { viewModel.selectTab(ArcTab.ARC) }
                    )
                    ArcTab.REVIEW -> ReviewScreen(viewModel = viewModel)
                    ArcTab.ARC -> ArcScreen(viewModel = viewModel)
                    ArcTab.BACKUP -> BackupScreen(viewModel = viewModel)
                }
            }
        }
    }
}
