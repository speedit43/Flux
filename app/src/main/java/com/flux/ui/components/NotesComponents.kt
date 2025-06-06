package com.flux.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flux.data.model.NotesModel
import com.flux.navigation.NavRoutes
import java.util.UUID

@Composable
fun NotesInputCard(
    innerPadding: PaddingValues,
    title: String,
    description: String,
    labels: List<String>,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLabelRemove: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
            .imePadding()
    ) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            placeholder = { Text("Title") },
            textStyle = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedTextColor = MaterialTheme.colorScheme.primary
            )
        )

        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("Description") },
            textStyle = MaterialTheme.typography.titleMedium,
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )

        if (labels.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            )
            {
                items(labels) { label ->
                    AssistChip(
                        onClick = { onLabelRemove(label) },
                        label = { Text(label) },
                        trailingIcon = { Icon(Icons.Default.Close, null) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotesHomeComponent(
    pinnedNotes: List<NotesModel>,
    unPinnedNotes: List<NotesModel>,
    radius: Int,
    isGridView: Boolean,
    selectedNotesId: List<UUID>,
    onClick: (String) -> Unit,
    onLongPressed: (NotesModel) -> Unit
){
    if (pinnedNotes.isNotEmpty()) {
        Text(
            "Pinned",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp).padding(top = 16.dp)
        )
        NotesPreviewGrid(
            radius,
            isGridView,
            pinnedNotes,
            selectedNotesId,
            onClick = onClick,
            onLongPressed = onLongPressed
        )
        Text(
            "Others",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )
    }
    if(pinnedNotes.isEmpty()) Spacer(Modifier.height(16.dp))
    NotesPreviewGrid(
        radius,
        isGridView,
        unPinnedNotes,
        selectedNotesId,
        onClick = onClick,
        onLongPressed = onLongPressed
    )
}

@Composable
fun NotesPreviewGrid(
    radius: Int,
    isGridView: Boolean,
    filteredNotes: List<NotesModel>,
    selectedNotesId: List<UUID>,
    onClick: (String) -> Unit,
    onLongPressed: (NotesModel) -> Unit
) {
    val columns = if (isGridView) GridCells.Fixed(2) else GridCells.Fixed(1)

    LazyVerticalGrid(
        columns = columns,
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = filteredNotes,
            key = { it.notesId }
        ) { note ->
            val isSelected = selectedNotesId.contains(note.notesId)
            NotesPreviewCard(
                radius = radius,
                isSelected = isSelected,
                isGridView = isGridView,
                note = note,
                onClick = onClick,
                onLongPressed = { onLongPressed(note) }
            )
        }
    }
}

@Composable
fun NotesPreviewCard(
    radius: Int,
    isSelected: Boolean,
    isGridView: Boolean,
    note: NotesModel,
    onClick: (String) -> Unit,
    onLongPressed: () -> Unit
) {
    val modifier = if (!isGridView) Modifier.fillMaxWidth() else Modifier

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier
            .clip(shapeManager(isBoth = true, radius = radius/2))
            .combinedClickable(
                onClick = { onClick(note.notesId.toString()) },
                onLongClick = onLongPressed
            )
            .heightIn(max=200.dp)
        ,
        shape = shapeManager(isBoth = true, radius = radius/2),
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else
            BorderStroke(0.1.dp, MaterialTheme.colorScheme.primary)
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

@Composable
fun EmptyNotes(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Notes,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text("No Notes Found")
    }
}
