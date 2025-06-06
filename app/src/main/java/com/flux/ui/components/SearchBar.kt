package com.flux.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.flux.data.model.NotesModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesSearchBar(
    isGridView: Boolean,
    textFieldState: TextFieldState,
    onSearch: (String) -> Unit,
    searchResults: List<NotesModel>,
    onSettingsClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    onGridViewChange: () -> Unit
) {
    val query = textFieldState.text.toString()
    var expanded by rememberSaveable { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        title = {},
        actions = {
            Box(Modifier.fillMaxSize().semantics { isTraversalGroup = true }) {
                SearchBar(
                    modifier = Modifier.align(Alignment.TopCenter).semantics { traversalIndex = 0f },
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = query,
                            onQueryChange = {
                                textFieldState.edit { replace(0, length, it) }
                                onSearch(it)
                                if(it.isBlank()){ expanded=false }
                            },
                            onSearch = { onSearch(query) },
                            expanded = expanded,
                            onExpandedChange = { expanded =it },
                            placeholder = { Text("Search") },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                            trailingIcon = {
                                Row {
                                    if (query.isNotBlank()) {
                                        CloseButton(contentDescription = "Clear") {
                                            textFieldState.edit { replace(0, length, "") }
                                            onCloseClicked()
                                            onSearch("")
                                            expanded=false
                                        }
                                    }
                                    GridViewButton(isGridView, onGridViewChange)
                                    if(!expanded){ SettingsButton(onSettingsClicked) }
                                }
                            },
                        )
                    },
                    colors = SearchBarDefaults.colors(containerColor = if(expanded) MaterialTheme.colorScheme.surfaceContainerLow else MaterialTheme.colorScheme.surfaceContainerHigh),
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        searchResults.forEach { note ->
                            Card(
                                modifier = if (!isGridView) Modifier.fillMaxWidth() else Modifier
                            ) {
                                Column(
                                    Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(2.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = note.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.alpha(0.9f)
                                    )
                                    Text(
                                        text = note.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.alpha(0.9f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}