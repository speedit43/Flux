package com.flux.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.RoundedCorner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.flux.R
import com.flux.ui.components.ActionType
import com.flux.ui.components.BasicScaffold
import com.flux.ui.components.SettingOption
import com.flux.ui.components.shapeManager
import com.flux.ui.events.SettingEvents
import com.flux.ui.state.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Customize(
    navController: NavController,
    settings: Settings,
    onSettingsEvents: (SettingEvents) -> Unit
) {
    val options = listOf("Low", "Medium", "High")

    BasicScaffold(
        title = stringResource(R.string.Customize),
        onBackClicked = { navController.popBackStack() }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding).padding(16.dp, 8.dp, 16.dp))
        {
            item{
                Text(stringResource(R.string.Themes), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(12.dp))

                SettingOption(
                    title = stringResource(R.string.System_theme),
                    description = stringResource(R.string.System_theme_desc),
                    icon = Icons.Filled.Settings,
                    radius = shapeManager(radius = settings.data.cornerRadius, isFirst = true),
                    actionType = ActionType.RADIOBUTTON,
                    variable = settings.data.isAutomaticTheme,
                    switchEnabled = { onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(isAutomaticTheme = true, isDarkMode = false, dynamicTheme = false, amoledTheme = false))) }
                )
            }

            item{
                SettingOption(
                    title = stringResource(R.string.Light_theme),
                    description = stringResource(R.string.Light_theme_desc),
                    icon = Icons.Filled.LightMode,
                    radius = shapeManager(radius = settings.data.cornerRadius),
                    actionType = ActionType.RADIOBUTTON,
                    variable = !settings.data.isAutomaticTheme && !settings.data.isDarkMode,
                    switchEnabled = { onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(isAutomaticTheme = false, isDarkMode = false))) }
                )
            }

            item{
                val isAutomatic=settings.data.isAutomaticTheme
                SettingOption(
                    title = stringResource(R.string.Dark_theme),
                    description = stringResource(R.string.Dark_theme_desc),
                    icon = Icons.Filled.DarkMode,
                    radius = shapeManager(radius = settings.data.cornerRadius, isLast = isAutomatic),
                    actionType = ActionType.RADIOBUTTON,
                    variable = !settings.data.isAutomaticTheme && settings.data.isDarkMode,
                    switchEnabled = { onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(isAutomaticTheme = false, isDarkMode = true))) }
                )
            }
            item{
                val isEnabled=!settings.data.isAutomaticTheme
                val isLightMode=!settings.data.isDarkMode && !settings.data.isAutomaticTheme
                SettingOption(
                    title = stringResource(R.string.Dynamic_theme),
                    description = stringResource(R.string.Dynamic_theme_desc),
                    icon = Icons.Filled.Colorize,
                    radius = shapeManager(radius = settings.data.cornerRadius, isLast = isLightMode),
                    actionType = ActionType.SWITCH,
                    isEnabled = isEnabled,
                    variable = settings.data.dynamicTheme,
                    switchEnabled = { onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(dynamicTheme = it))) },
                )
            }

            item{
                val isEnabled = settings.data.isDarkMode
                SettingOption(
                    title = stringResource(R.string.Amoled_theme),
                    description = stringResource(R.string.Amoled_theme_desc),
                    icon = Icons.Filled.DarkMode,
                    isEnabled = isEnabled,
                    radius = shapeManager(radius = settings.data.cornerRadius, isLast = true),
                    actionType = ActionType.SWITCH,
                    variable = settings.data.amoledTheme,
                    switchEnabled = { onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(amoledTheme = it))) },
                )
            }

            item{
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.Shape), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(12.dp))

                SettingOption(
                    title = stringResource(R.string.Radius),
                    description = stringResource(R.string.Radius_desc),
                    icon = Icons.Rounded.RoundedCorner,
                    radius = shapeManager(
                        radius = settings.data.cornerRadius,
                        isFirst = true
                    ),
                    actionType = ActionType.CUSTOM,
                    customAction = { onExit ->
                        OnRadiusClicked(settings) {
                            onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(cornerRadius = it)))
                            onExit()
                        }
                    }
                )

                SettingOption(
                    title = "Grid View",
                    description = "Change Notes View to Grid",
                    icon = Icons.Rounded.GridView,
                    radius = shapeManager(
                        radius = settings.data.cornerRadius,
                        isLast = true
                    ),
                    variable = settings.data.isGridView,
                    actionType = ActionType.SWITCH,
                    switchEnabled = { onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(isGridView = it))) },
                )
            }

            item{
                Spacer(Modifier.height(12.dp))
                AnimatedVisibility(visible = !settings.data.dynamicTheme) {
                    Text(stringResource(R.string.Contrast), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.height(12.dp))

                AnimatedVisibility(visible = !settings.data.dynamicTheme) {

                    SingleChoiceSegmentedButtonRow {
                        options.forEachIndexed { index, label ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                onClick = { onSettingsEvents(SettingEvents.UpdateSettings(settings.data.copy(contrast = index))) },
                                selected = index == settings.data.contrast,
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnRadiusClicked( settings: Settings, onExit: (Int) -> Unit) {
    val minimalRadius = 5
    val settingsRadius = settings.data.cornerRadius
    var sliderPosition by remember { mutableFloatStateOf(((settingsRadius - minimalRadius).toFloat()/30)) }
    val realRadius : Int  = (((sliderPosition*100).toInt())/3) + minimalRadius

    @Composable
    fun example(shape: RoundedCornerShape) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp, 3.dp, 32.dp, 1.dp)
                .background(
                    shape = shape,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )
                .height(62.dp),
        )
    }
    Dialog(onDismissRequest = { onExit(realRadius) }) {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(realRadius / 3)
                )
                .fillMaxWidth()
                .fillMaxSize(0.38f)
        ) {
            Text(
                text = stringResource(R.string.Select_radius),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
            )
            example(shapeManager(radius = realRadius, isFirst = true))
            example(shapeManager(radius = realRadius))
            example(shapeManager(radius = realRadius, isLast = true))
            Slider(
                value = sliderPosition,
                modifier = Modifier.padding(32.dp, 16.dp, 32.dp, 16.dp),
                colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                onValueChange = { newValue -> sliderPosition = newValue}
            )
        }
    }
}