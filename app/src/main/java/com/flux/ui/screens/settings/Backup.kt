package com.flux.ui.screens.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Backup
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.ui.components.ActionType
import com.flux.ui.components.BasicScaffold
import com.flux.ui.components.SettingOption
import com.flux.ui.components.shapeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Backup(navController: NavController, radius: Int) {
    BasicScaffold(
        title = "Backup",
        onBackClicked = { navController.popBackStack() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp, 8.dp, 16.dp)
        ) {
            item {
                SettingOption(
                    title = "Backup",
                    description = "Backup your data to a file",
                    icon = Icons.Rounded.Backup,
                    radius = shapeManager(radius = radius, isFirst = true),
                    actionType = ActionType.None
                )
            }

            item {
                SettingOption(
                    title = "Restore",
                    description = "Restore your data from backup file",
                    icon = Icons.Rounded.Restore,
                    radius = shapeManager(radius = radius, isLast = true),
                    actionType = ActionType.None
                )
            }
        }
    }
}

