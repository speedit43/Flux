package com.flux.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.flux.R
import com.flux.data.model.WorkspaceModel
import com.flux.ui.events.WorkspaceEvents

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceSearchBar(
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<WorkspaceModel>,
    onSettingsClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onWorkspaceEvents: (WorkspaceEvents)->Unit,
    onClick: (WorkspaceModel)->Unit
){
    val query = textFieldState.text.toString()
    var expanded by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier.align(Alignment.TopCenter).wrapContentHeight().semantics { traversalIndex = 0f },
            inputField = {
                WorkspaceSearchInputField(query, expanded,
                    onQueryChange = {
                        textFieldState.edit { replace(0, length, it) }
                        onSearch(it)
                        if (it.isBlank()) { expanded = false }
                    },
                    onSearch={
                        keyboardController?.hide()
                        onSearch(query)
                    },
                    onSearchClosed = {
                        textFieldState.edit { replace(0, length, "") }
                        onCloseClicked()
                        onSearch("")
                        expanded = false
                    },
                    onExpandedChange = { expanded = it },
                    onSettingsClicked = onSettingsClicked
                )
            },
            colors = SearchBarDefaults.colors(containerColor = if (expanded) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
            ) {
                LazyColumn {
                    itemsIndexed(searchResults) { index, space ->
                        WorkSpacesCard(
                            workspace = space,
                            onClick = {
                                expanded = false
                                onClick(space)
                            },
                            onDeleteClick = {

                            },
                            onWorkspaceEvents = onWorkspaceEvents
                        )
                        if (index!=searchResults.lastIndex){ HorizontalDivider(modifier = Modifier.alpha(0.4f)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkspaceSearchInputField(
    query: String,
    expanded: Boolean,
    onQueryChange: (String)->Unit,
    onSearch: (String)->Unit,
    onSearchClosed: ()-> Unit,
    onExpandedChange: (Boolean)->Unit,
    onSettingsClicked: ()->Unit
){
    SearchBarDefaults.InputField(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        placeholder = { Text(stringResource(R.string.Search_Workspaces)) },
        leadingIcon = { Icon(Icons.Rounded.Search, "Search") },
        trailingIcon = {
            Row { if(query.isNotBlank() || expanded) CloseButton(onSearchClosed)
            if(!expanded) {SettingsButton( onSettingsClicked )} }
        }
    )
}