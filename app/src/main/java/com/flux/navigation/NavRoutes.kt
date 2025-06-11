package com.flux.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.flux.data.model.NotesModel
import com.flux.ui.screens.calender.Calender
import com.flux.ui.screens.notes.EditLabels
import com.flux.ui.screens.notes.NoteDetails
import com.flux.ui.screens.settings.About
import com.flux.ui.screens.settings.Contact
import com.flux.ui.screens.settings.Customize
import com.flux.ui.screens.settings.Languages
import com.flux.ui.screens.settings.Privacy
import com.flux.ui.screens.settings.Settings
import com.flux.ui.screens.tasks.TaskDetails
import com.flux.ui.screens.workspaces.WorkSpaces
import com.flux.ui.screens.workspaces.WorkspaceDetails
import com.flux.ui.state.States
import com.flux.ui.viewModel.ViewModels
import kotlin.String
import kotlin.Unit


sealed class NavRoutes(val route: String) {
    // workspaces
    data object Workspace : NavRoutes ("workspace")
    data object WorkspaceHome : NavRoutes ("workspace/details")

    //Labels
    data object EditLabels : NavRoutes ("workspace/labels/edit")

    // Notes
    data object NoteDetails : NavRoutes("workspace/notes/details")

    // Tasks
    data object TaskHome : NavRoutes("workspace/tasks/home")
    data object TaskDetails : NavRoutes("workspace/tasks/details")

    // Calender
    data object Calender : NavRoutes ("workspace/calender/home")

    // Settings
    data object Settings : NavRoutes("settings")
    data object Privacy : NavRoutes("settings/privacy")
    data object Customize : NavRoutes("settings/customize")
    data object Languages : NavRoutes("settings/language")
    data object About : NavRoutes("settings/about")
    data object Contact : NavRoutes("settings/contact")

    fun withArgs(vararg args: Int): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}

val NotesScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState, notesId: Int, workspaceId: Int, states: States, viewModels: ViewModels) -> Unit>(
        NavRoutes.NoteDetails.route + "/{workspaceId}" + "/{notesId}" to { navController, snackbarHostState, notesId, workspaceId, states, viewModel ->
            NoteDetails(navController, workspaceId, states.notesState.allNotes.find { it.notesId==notesId }?: NotesModel(workspaceId=workspaceId), states.labelState.data.filter { it.workspaceId==workspaceId }, snackbarHostState, viewModel.notesViewModel::onEvent)
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
        NavRoutes.Contact.route to { navController, snackbarHostState, states, _ ->
            Contact(navController, states.settings.data.cornerRadius)
        }
    )

val TasksScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState) -> Unit> (
        NavRoutes.TaskDetails.route to { navController, snackbarHostState ->
            TaskDetails(navController)
        }
    )

val CalenderScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState) -> Unit> (
        NavRoutes.Calender.route to { navController, snackbarHostState ->
            Calender(navController)
        }
    )

val WorkspaceScreens =
    mapOf<String, @Composable (navController: NavController, snackbarHostState: SnackbarHostState, states: States, viewModels: ViewModels, workspaceId: Int) -> Unit> (
        NavRoutes.Workspace.route to { navController, snackbarHostState, states, viewModels, _ ->
            WorkSpaces(snackbarHostState, navController, states.workspaceState.allSpaces, viewModels.workspaceViewModel::onEvent)
        },
        NavRoutes.WorkspaceHome.route + "/{workspaceId}" to { navController, snackbarHostState, states, viewModels, workspaceId ->
            WorkspaceDetails(navController, states.labelState.data.filter { it.workspaceId==workspaceId }, states.settings, states.workspaceState.isLoading, states.workspaceState.allSpaces.first { it.workspaceId==workspaceId }, states.notesState.allNotes.filter { it.workspaceId==workspaceId }, viewModels.workspaceViewModel::onEvent, viewModels.notesViewModel::onEvent, viewModels.settingsViewModel::onEvent)
        }
    )

val LabelScreens =
    mapOf<String, @Composable (navController: NavController, states: States, viewModels: ViewModels, workspaceId: Int) -> Unit> (
        NavRoutes.EditLabels.route + "/{workspaceId}" to { navController, states, viewModels, workspaceId ->
            EditLabels(navController, workspaceId, states.labelState.data.filter { it.workspaceId==workspaceId }, viewModels.labelViewModel::onEvent)
        }
    )
