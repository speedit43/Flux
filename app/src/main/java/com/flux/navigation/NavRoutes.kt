package com.flux.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.flux.ui.screens.calender.Calender
import com.flux.ui.screens.notes.NoteDetails
import com.flux.ui.screens.notes.NotesHome
import com.flux.ui.screens.settings.About
import com.flux.ui.screens.settings.Contact
import com.flux.ui.screens.settings.Customize
import com.flux.ui.screens.settings.Languages
import com.flux.ui.screens.settings.Privacy
import com.flux.ui.screens.settings.Settings
import com.flux.ui.screens.tasks.TaskHome
import com.flux.ui.state.States
import com.flux.ui.viewModel.ViewModels
import java.util.UUID
import kotlin.String
import kotlin.Unit

sealed class NavRoutes(val route: String) {
    // Notes
    data object NotesHome : NavRoutes("notes/home")
    data object NoteDetails : NavRoutes("notes/details")

    // Tasks
    data object TaskHome : NavRoutes("tasks/home")

    // Calender
    data object Calender : NavRoutes ("calender/home")

    // Settings
    data object Settings : NavRoutes("settings")
    data object Privacy : NavRoutes("settings/privacy")
    data object Customize : NavRoutes("settings/customize")
    data object Languages : NavRoutes("settings/language")
    data object About : NavRoutes("settings/about")
    data object Contact : NavRoutes("settings/contact")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { args ->
                append("/$args")
            }
        }
    }
}

val NotesScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState, notesId: UUID?, states: States, viewModels: ViewModels) -> Unit> (
        NavRoutes.NotesHome.route to { navController, snackbarHostState, _, states, viewModels ->
            NotesHome(navController, states.settings, states.labelState.isLoading || states.notesState.isLoading, states.settings.data.cornerRadius, states.notesState.allNotes, states.labelState.data, snackbarHostState, viewModels.notesViewModel::onEvent, viewModels.labelViewModel::onEvent, viewModels.settingsViewModel::onEvent)
        },
        NavRoutes.NoteDetails.route to { navController, snackbarHostState, notesId, states, viewModel ->
            NoteDetails(navController, states.labelState.data, states.notesState.allNotes, notesId, snackbarHostState, viewModel.notesViewModel::onEvent)
        },
        NavRoutes.NoteDetails.route + "/{notesId}" to { navController, snackbarHostState, notesId, states, viewModel ->
            NoteDetails(navController, states.labelState.data, states.notesState.allNotes, notesId, snackbarHostState, viewModel.notesViewModel::onEvent)
        }
    )

val SettingsScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState, states: States, viewModels: ViewModels) -> Unit> (
        NavRoutes.Settings.route to { navController, snackbarHostState, states, viewModels ->
            Settings(navController, states.settings, snackbarHostState)
        },
        NavRoutes.Privacy.route to { navController, _, states, viewModels ->
            Privacy(navController, states.settings, viewModels.settingsViewModel::onEvent)
        },
        NavRoutes.About.route to { navController, snackbarHostState, states, _ ->
            About(navController, states.settings.data.cornerRadius)
        },
        NavRoutes.Languages.route to { navController, snackbarHostState, states, _ ->
            Languages(navController, states.settings)
        },
        NavRoutes.Customize.route to { navController, snackbarHostState, states, viewModels ->
            Customize(navController, states.settings, viewModels.settingsViewModel::onEvent)
        },
        NavRoutes.Contact.route to { navController, snackbarHostState, _, _ ->
            Contact(navController)
        }
    )

val TasksScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState) -> Unit> (
        NavRoutes.TaskHome.route to { navController, snackbarHostState ->
            TaskHome(navController)
        }
    )

val CalenderScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState) -> Unit> (
        NavRoutes.Calender.route to { navController, snackbarHostState ->
            Calender(navController)
        }
    )
