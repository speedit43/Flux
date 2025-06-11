package com.flux.ui.screens.calender

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.navigation.NavRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calender(navController: NavController){
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow,),
                title = {},
                actions = {
                    IconButton(onClick = {navController.navigate(NavRoutes.Settings.route)}) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
//        LazyColumn(modifier = Modifier.padding(innerPadding)) {
//            item{
//
//            }
//        }
        Column(
            Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(48.dp))
            Text("Calender")
        }
    }
}