package com.flux.ui.screens.workspaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.data.model.WorkspaceModel
import com.flux.navigation.NavRoutes
import com.flux.ui.components.NewWorkspaceBottomSheet
import com.flux.ui.components.WorkSpacesCard
import com.flux.ui.components.WorkspaceSelectedBar
import com.flux.ui.components.WorkspaceTopBar
import com.flux.ui.events.WorkspaceEvents
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkSpaces(
    snackbarHostState: SnackbarHostState,
    navController: NavController,
    allSpaces: List<WorkspaceModel>,
    onWorkSpaceEvents: (WorkspaceEvents) -> Unit
){
    var query by rememberSaveable { mutableStateOf("") }
    var addWorkspace by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val selectedSpacesIds = remember { mutableStateListOf<Int>() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            if(selectedSpacesIds.isEmpty()){
                WorkspaceTopBar(
                    textFieldState = TextFieldState(query),
                    onSearch = { query = it },
                    searchResults = allSpaces,
                    onCloseClicked = { query = "" },
                    onSettingsClicked = { navController.navigate(NavRoutes.Settings.route) }
                )
            }
            else{
                WorkspaceSelectedBar(selectedSpacesIds.size, selectedSpacesIds.size==allSpaces.size,
                    onClose = { selectedSpacesIds.clear() }, onSelectAll = {
                        allSpaces.forEach {
                            if (!selectedSpacesIds.contains(it.workspaceId)){
                                selectedSpacesIds.add(it.workspaceId)
                            }
                        }
                    }, onDelete = {
                        onWorkSpaceEvents(WorkspaceEvents.DeleteSpaces(selectedSpacesIds.toList()))
                        selectedSpacesIds.clear()
                    })
            }
        },
        floatingActionButton = { if(selectedSpacesIds.isEmpty()) FloatingActionButton(onClick = { addWorkspace=true }) { Icon(Icons.Default.Add, null) } },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ){
            items(allSpaces) { space->
                WorkSpacesCard(workspace = space, onClick = { navController.navigate(NavRoutes.WorkspaceHome.withArgs(space.workspaceId) )}, isSelected = selectedSpacesIds.contains(space.workspaceId), onLongPress = {
                    if(selectedSpacesIds.contains(it)) selectedSpacesIds.remove(it)
                    else selectedSpacesIds.add(it)
                })
            }
        }

        NewWorkspaceBottomSheet(isVisible = addWorkspace, sheetState = sheetState, onDismiss = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    addWorkspace=false
                }
            }
        }, onConfirm = { onWorkSpaceEvents(WorkspaceEvents.UpsertSpace(it)) })
    }
}