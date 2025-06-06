package com.flux.ui.screens.settings

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.BuildConfig
import com.flux.R
import com.flux.ui.components.BasicScaffold
import com.flux.ui.components.SettingCategory
import com.flux.ui.components.SettingIcon
import com.flux.ui.components.SingleSettingOption
import com.flux.ui.components.shapeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun About(navController: NavController, radius: Int){

    BasicScaffold(
        title = stringResource(R.string.About),
        onBackClicked = { navController.popBackStack() }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp, 8.dp, 16.dp)) {
            item{
                SingleSettingOption(
                    radius,
                    stringResource(R.string.app_name),
                    leadingIcon = SettingIcon.Resource(R.mipmap.ic_launcher_foreground),
                    textStyle = MaterialTheme.typography.titleLarge,
                    last = true
                ) { }
            }

            item{
                SettingCategory(
                    title = stringResource(R.string.Build_type),
                    subTitle = BuildConfig.BUILD_TYPE.uppercase(),
                    icon = Icons.Rounded.Build,
                    shape = shapeManager(radius = radius, isFirst = true),
                    action = { }
                )
            }

            item{
                SettingCategory(
                    title = stringResource(R.string.Build_version),
                    subTitle = BuildConfig.VERSION_NAME,
                    icon = Icons.Rounded.Info,
                    shape = shapeManager(radius = radius, isLast = true),
                    action = { }
                )
            }
        }
    }
}

