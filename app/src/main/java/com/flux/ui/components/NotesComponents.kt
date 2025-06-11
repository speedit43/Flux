package com.flux.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LabelImportant
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flux.data.model.LabelModel
import com.flux.data.model.NotesModel

@Composable
fun NotesInputCard(
    innerPadding: PaddingValues,
    title: String,
    description: String,
    allLabels: List<LabelModel>,
    onLabelClicked: ()->Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp).imePadding()) {
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
                focusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.primary
            )
        )

        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("Description") },
            textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraLight),
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 12.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedTextColor = MaterialTheme.colorScheme.primary,
                focusedPlaceholderColor = MaterialTheme.colorScheme.primary
            )
        )

        if(allLabels.isNotEmpty()){
            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                items(allLabels) { label->
                    Box(modifier= Modifier.clip(RoundedCornerShape(50)).background(MaterialTheme.colorScheme.primaryContainer).clickable{onLabelClicked()}) {
                        Row(modifier= Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            Icon(Icons.AutoMirrored.Default.LabelImportant, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(label.value, color = MaterialTheme.colorScheme.onPrimaryContainer, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesSelectedBar(
    totalSelectedNotes: Int,
    isAllSelected: Boolean,
    isAllPinned: Boolean=false,
    onSelectAll: ()->Unit,
    onPinClicked: ()->Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(top = 46.5.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onClose) { Icon(Icons.Default.Close, null) }
            Text(totalSelectedNotes.toString(), style = MaterialTheme.typography.titleLarge)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if(!isAllSelected){ IconButton(onClick = onSelectAll ) { Icon(Icons.Default.SelectAll, null) } }
            IconButton(onClick = onPinClicked) { Icon(if(isAllPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, null)  }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null) }
        }
    }
}

@Composable
fun NotesPreviewGrid(
    radius: Int,
    isGridView: Boolean,
    allLabels: List<LabelModel>,
    pinnedNotes: List<NotesModel>,
    unPinnedNotes: List<NotesModel>,
    selectedNotes: List<NotesModel>,
    onClick: (Int) -> Unit,
    onLongPressed: (NotesModel) -> Unit
) {
    val columns = if (isGridView) GridCells.Fixed(2) else GridCells.Fixed(1)
    val showPinnedText=!pinnedNotes.isEmpty()
    LazyVerticalGrid(
        columns = columns,
        modifier = Modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (showPinnedText) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Pinned",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp).padding(top = 16.dp)
                )
            }
        }

        if(!showPinnedText){
            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(Modifier.height(8.dp)) }
        }

        items(
            items = pinnedNotes,
            key = { it.notesId }
        ) { note ->
            val isSelected = selectedNotes.contains(note)

            NotesPreviewCard(
                radius = radius,
                isSelected = isSelected,
                isGridView = isGridView,
                note = note,
                onClick = onClick,
                labels = allLabels.filter { note.labels.contains(it.labelId) }.map { it.value },
                onLongPressed = { onLongPressed(note) }
            )
        }

        if (showPinnedText) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    "Others",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
                )
            }
        }

        items(
            items = unPinnedNotes,
            key = { it.notesId }
        ) { note ->
            val isSelected = selectedNotes.contains(note)

            NotesPreviewCard(
                radius = radius,
                isSelected = isSelected,
                isGridView = isGridView,
                note = note,
                labels = allLabels.filter { note.labels.contains(it.labelId) }.map { it.value },
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
    labels: List<String>,
    onClick: (Int) -> Unit,
    onLongPressed: () -> Unit
) {
    val modifier = if (!isGridView) Modifier.fillMaxWidth() else Modifier

    Card(
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
        modifier = modifier
            .clip(shapeManager(isBoth = true, radius = radius/2))
            .combinedClickable(
                onClick = { onClick(note.notesId) },
                onLongClick = onLongPressed
            ),
        shape = shapeManager(isBoth = true, radius = radius/2),
        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
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
                modifier = Modifier.alpha(0.9f),
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )
            val maxVisibleLabels = 2
            val visibleLabels = labels.take(maxVisibleLabels)
            val extraCount = labels.size - maxVisibleLabels

            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                visibleLabels.forEach { label ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Default.LabelImportant, null, modifier = Modifier.size(15.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(
                                label,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.labelMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (extraCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = "+$extraCount",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

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
