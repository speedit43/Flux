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
import com.flux.ui.viewModel.LabelViewModel
import com.flux.ui.viewModel.NotesViewModel
import com.flux.ui.viewModel.SettingsViewModel
import com.flux.ui.viewModel.ViewModels
import java.util.UUID

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState,
    labelViewModel: LabelViewModel,
    settingsViewModel: SettingsViewModel,
    notesViewModel: NotesViewModel,
    settings: Settings,
    notesState: NotesState,
    labelState: LabelState
) {

    NavHost(navController, startDestination = NavRoutes.NotesHome.route) {

        NotesScreens.forEach { (route, screen) ->
            val arguments = mutableListOf<NamedNavArgument>()

            if (route.contains("{notesId}")) {
                arguments.add(navArgument("notesId") {
                    type = NavType.StringType
                    nullable = false
                })
            }

            animatedComposable(route, arguments) { entry ->
                val id = entry.arguments?.getString("notesId")?.let(UUID::fromString)
                screen(navController, snackbarHostState, id,  States(notesState, labelState, settings), ViewModels(labelViewModel, notesViewModel, settingsViewModel))
            }
        }

        SettingsScreens.forEach { (route, screen)->
            if (route == NavRoutes.Settings.route){
                slideInComposable(route) { screen(navController, snackbarHostState, States(notesState, labelState, settings), ViewModels(labelViewModel, notesViewModel, settingsViewModel)) }
            }
            else{
                animatedComposable(route) { screen(navController, snackbarHostState, States(notesState, labelState, settings), ViewModels(labelViewModel, notesViewModel, settingsViewModel)) }
            }
        }

        TasksScreens.forEach { (route, screen) ->
            animatedComposable(route) { screen(navController, snackbarHostState) }
        }

        CalenderScreens.forEach { (route, screen) ->
            animatedComposable(route) { screen(navController, snackbarHostState) }
        }
    }
}