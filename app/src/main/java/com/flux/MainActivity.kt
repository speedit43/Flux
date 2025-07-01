package com.flux


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flux.navigation.AppNavHost
import com.flux.navigation.Loader
import com.flux.other.createNotificationChannel
import com.flux.ui.effects.ScreenEffect
import com.flux.ui.theme.FluxTheme
import com.flux.ui.viewModel.HabitViewModel
import com.flux.ui.viewModel.NotesViewModel
import com.flux.ui.viewModel.SettingsViewModel
import com.flux.ui.viewModel.EventViewModel
import com.flux.ui.viewModel.TodoViewModel
import com.flux.ui.viewModel.WorkspaceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var keepSplashScreen = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Splash screen condition
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen.value }

        enableEdgeToEdge()

        setContent {
            createNotificationChannel(this)
            val snackBarHostState = remember { SnackbarHostState() }

            // ViewModels
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val notesViewModel: NotesViewModel = hiltViewModel()
            val workspaceViewModel: WorkspaceViewModel = hiltViewModel()
            val eventViewModel: EventViewModel = hiltViewModel()
            val habitViewModel: HabitViewModel = hiltViewModel()
            val todoViewModel: TodoViewModel = hiltViewModel()

            // States
            val settings by settingsViewModel.state.collectAsState()
            val notesState by notesViewModel.state.collectAsStateWithLifecycle()
            val workspaceState by workspaceViewModel.state.collectAsStateWithLifecycle()
            val eventState by eventViewModel.state.collectAsStateWithLifecycle()
            val habitState by habitViewModel.state.collectAsStateWithLifecycle()
            val todoState by todoViewModel.state.collectAsStateWithLifecycle()

            // Stop splash screen when settings are loaded
            LaunchedEffect(settings.isLoading) {
                keepSplashScreen.value = settings.isLoading
            }

            // Snackbar effect
            LaunchedEffect(Unit) {
                workspaceViewModel.effect.collect { effect ->
                    if (effect is ScreenEffect.ShowSnackBarMessage) {
                        snackBarHostState.currentSnackbarData?.dismiss()
                        snackBarHostState.showSnackbar(
                            message = effect.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }

            if (!settings.isLoading) {
                FluxTheme(settings) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceContainerLow
                    ) {
                        AppNavHost(
                            snackbarHostState = snackBarHostState,
                            settingsViewModel = settingsViewModel,
                            notesViewModel = notesViewModel,
                            workspaceViewModel = workspaceViewModel,
                            eventViewModel = eventViewModel,
                            habitViewModel = habitViewModel,
                            todoViewModel = todoViewModel,
                            settings = settings,
                            notesState = notesState,
                            workspaceState = workspaceState,
                            eventState = eventState,
                            habitState = habitState,
                            todoState = todoState
                        )
                    }
                }
            } else {
                Loader()
            }
        }
    }
}