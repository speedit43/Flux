package com.flux

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.flux.navigation.AppNavHost
import com.flux.ui.theme.FluxTheme
import com.flux.ui.viewModel.LabelViewModel
import com.flux.ui.viewModel.NotesViewModel
import com.flux.ui.viewModel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        setContent {
            val snackBarHostState = remember { SnackbarHostState() }
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val settings by settingsViewModel.state.collectAsState()
            val notesViewModel: NotesViewModel = hiltViewModel()
            val notesState by notesViewModel.state.collectAsState()
            val labelViewModel: LabelViewModel = hiltViewModel()
            val labelState by labelViewModel.state.collectAsState()

            FluxTheme(settings) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    AppNavHost(
                        snackbarHostState = snackBarHostState,
                        settingsViewModel = settingsViewModel,
                        notesViewModel = notesViewModel,
                        labelViewModel = labelViewModel,
                        settings = settings,
                        notesState = notesState,
                        labelState = labelState
                    )
                }
            }
        }
    }
}

