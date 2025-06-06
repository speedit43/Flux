package com.flux.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.flux.R
import com.flux.ui.components.BasicScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contact(navController: NavController){
    BasicScaffold(
        title = stringResource(R.string.Contact),
        onBackClicked = { navController.popBackStack() }
    ) { innerPadding ->
        Column(
            Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Contact Screen")
        }
    }
}