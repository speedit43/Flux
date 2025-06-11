package com.flux.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.flux.ui.state.LabelState
import com.flux.ui.state.NotesState
import com.flux.ui.state.Settings
import com.flux.ui.state.States
import com.flux.ui.state.WorkspaceState
import com.flux.ui.viewModel.LabelViewModel
import com.flux.ui.viewModel.NotesViewModel
import com.flux.ui.viewModel.SettingsViewModel
import com.flux.ui.viewModel.ViewModels
import com.flux.ui.viewModel.WorkspaceViewModel

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState,
    labelViewModel: LabelViewModel,
    settingsViewModel: SettingsViewModel,
    notesViewModel: NotesViewModel,
    workspaceViewModel: WorkspaceViewModel,
    settings: Settings,
    notesState: NotesState,
    labelState: LabelState,
    workspaceState: WorkspaceState
) {

    NavHost(navController, startDestination = NavRoutes.Workspace.route) {
        NotesScreens.forEach { (route, screen) ->
            val arguments = mutableListOf<NamedNavArgument>()

            if (route.contains("{notesId}")) {
                arguments.add(navArgument("notesId") {
                    type = NavType.IntType
                    nullable = false
                })
            }

            if (route.contains("{workspaceId}")) {
                arguments.add(navArgument("workspaceId") {
                    type = NavType.IntType
                    nullable = false
                })
            }

            animatedComposable(route, arguments) { entry ->
                val notesId = entry.arguments?.getInt("notesId") ?: 0
                val workspaceId = entry.arguments?.getInt("workspaceId") ?: 0

                screen(navController, snackbarHostState, notesId, workspaceId, States(notesState, labelState, workspaceState, settings), ViewModels(labelViewModel, notesViewModel, workspaceViewModel, settingsViewModel))
            }
        }

        SettingsScreens.forEach { (route, screen)->
            if (route == NavRoutes.Settings.route){
                slideInComposable(route) { screen(navController, snackbarHostState, States(notesState, labelState, workspaceState, settings), ViewModels(labelViewModel, notesViewModel, workspaceViewModel, settingsViewModel)) }
            }
            else{
                animatedComposable(route) { screen(navController, snackbarHostState, States(notesState, labelState, workspaceState, settings), ViewModels(labelViewModel, notesViewModel, workspaceViewModel, settingsViewModel)) }
            }
        }

        TasksScreens.forEach { (route, screen) ->
            bottomSlideComposable(route) { screen(navController, snackbarHostState) }
        }

        CalenderScreens.forEach { (route, screen) ->
            animatedComposable(route) { screen(navController, snackbarHostState) }
        }

        LabelScreens.forEach { (route, screen) ->
            val arguments = mutableListOf<NamedNavArgument>()

            if (route.contains("{workspaceId}")) {
                arguments.add(navArgument("workspaceId") {
                    type = NavType.IntType
                    nullable = false
                })
            }

            bottomSlideComposable(route, arguments) { entry->
                val workspaceId = entry.arguments?.getInt("workspaceId") ?: 0

                screen(navController, States(notesState, labelState, workspaceState, settings), ViewModels(labelViewModel, notesViewModel, workspaceViewModel, settingsViewModel), workspaceId)
            }
        }

        WorkspaceScreens.forEach { (route, screen) ->
            val arguments = mutableListOf<NamedNavArgument>()

            if (route.contains("{workspaceId}")) {
                arguments.add(navArgument("workspaceId") {
                    type = NavType.IntType
                    nullable = false
                })
            }

            animatedComposable(route, arguments) { entry ->
                val id = entry.arguments?.getInt("workspaceId") ?: 0
                screen(navController, snackbarHostState, States(notesState, labelState, workspaceState, settings), ViewModels(labelViewModel, notesViewModel, workspaceViewModel, settingsViewModel), id)
            }
        }
    }
}

