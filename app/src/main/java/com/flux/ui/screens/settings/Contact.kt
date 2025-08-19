package com.flux.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.flux.R
import com.flux.ui.components.ActionType
import com.flux.ui.components.BasicScaffold
import com.flux.ui.components.SettingOption
import com.flux.ui.components.shapeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contact(navController: NavController, radius: Int) {
    val context = LocalContext.current

    BasicScaffold(
        title = stringResource(R.string.Contact),
        onBackClicked = { navController.popBackStack() }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp, 8.dp, 16.dp)
        ) {
            item {
                SettingOption(
                    title = stringResource(R.string.Contact_desc),
                    description = stringResource(R.string.Contact_desc2),
                    icon = Icons.Rounded.Feedback,
                    radius = shapeManager(radius = radius, isBoth = true),
                    actionType = ActionType.LINK,
                    linkClicked = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://github.com/chindaronit/Flux/issues".toUri()
                        )
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}