package com.flux.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {
    val screens = mutableListOf(
        BottomBarItem.NotesHome,
        BottomBarItem.TasksHome,
        BottomBarItem.Calender,
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: BottomBarItem,
    currentDestination: NavDestination?,
    navController: NavController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    NavigationBarItem(
        label = { Text(text = screen.title, style = MaterialTheme.typography.labelMedium) },
        icon = {
            if (selected) {
                Icon(imageVector = screen.selectedIcon, contentDescription = "Navigation Icon", modifier = Modifier.alpha(0.9f))
            } else {
                Icon(imageVector = screen.unselectedIcon, contentDescription = "Navigation Icon", modifier = Modifier.alpha(0.9f))
            }
        },
        selected = selected,
        onClick = {
            if (currentDestination?.route != screen.route) {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
        }
    )
}

sealed class BottomBarItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    data object NotesHome : BottomBarItem(
        "Notes",
        NavRoutes.NotesHome.route,
        Icons.AutoMirrored.Filled.Notes,
        Icons.AutoMirrored.Outlined.Notes
    )
    data object TasksHome : BottomBarItem(
        "Tasks",
        NavRoutes.TaskHome.route,
        Icons.Filled.TaskAlt,
        Icons.Outlined.TaskAlt
    )
    data object Calender : BottomBarItem(
        "Calender",
        NavRoutes.Calender.route,
        Icons.Filled.CalendarMonth,
        Icons.Outlined.CalendarMonth
    )
}

