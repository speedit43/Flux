package com.flux.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.LabelImportant
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NewLabel
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flux.data.model.LabelModel
import com.flux.data.model.WorkspaceModel

@Composable
fun AddLabelDialog(
    initialValue: String,
    onConfirmation: (String)->Unit,
    onDismissRequest: ()-> Unit
){
    var label by remember { mutableStateOf(initialValue) }

    AlertDialog(
        icon = { CircleWrapper(MaterialTheme.colorScheme.primary) { val icon=if(initialValue.isBlank()) Icons.Default.Add else Icons.Default.Edit
            Icon(icon, contentDescription = "Add/Edit Icon", tint = MaterialTheme.colorScheme.onPrimary) } },
        title = { Text(text = if(initialValue.isBlank()) "Add New Label" else "Edit Label") },
        text = { OutlinedTextField(value = label, onValueChange = { label=it }, singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    onConfirmation(label)
                    onDismissRequest()
                }
            )) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation(label)
                    onDismissRequest() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
            ) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text("Dismiss") } }
    )
}

@Composable
fun AddSpacesDialog(
    workspace: WorkspaceModel,
    onDismissRequest: () -> Unit,
    onConfirmation: (WorkspaceModel) -> Unit
) {
    // Local checkbox states for not-yet-added modules
    var notesChecked by remember { mutableStateOf(false) }
    var taskChecked by remember { mutableStateOf(false) }
    var calendarChecked by remember { mutableStateOf(false) }
    var analyticsChecked by remember { mutableStateOf(false) }

    AlertDialog(
        icon = {
            CircleWrapper(MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Label Icon", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        title = { Text(text = "Add Spaces") },
        text = {
            LazyColumn {
                if (!workspace.isNotesAdded) {
                    item {
                        SpaceCheckboxCard(Icons.AutoMirrored.Default.Notes, "Notes", notesChecked) {
                            notesChecked = it
                        }
                    }
                }
                if (!workspace.isTaskAdded) {
                    item {
                        SpaceCheckboxCard(Icons.Default.TaskAlt, "Tasks", taskChecked) {
                            taskChecked = it
                        }
                    }
                }
                if (!workspace.isCalenderAdded) {
                    item {
                        SpaceCheckboxCard(Icons.Default.CalendarMonth, "Calendar", calendarChecked) {
                            calendarChecked = it
                        }
                    }
                }
                if (!workspace.isAnalyticsAdded) {
                    item {
                        SpaceCheckboxCard(Icons.Default.Analytics, "Analytics", analyticsChecked) {
                            analyticsChecked = it
                        }
                    }
                }
            }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    val updated = workspace.copy(
                        isNotesAdded = workspace.isNotesAdded || notesChecked,
                        isTaskAdded = workspace.isTaskAdded || taskChecked,
                        isCalenderAdded = workspace.isCalenderAdded || calendarChecked,
                        isAnalyticsAdded = workspace.isAnalyticsAdded || analyticsChecked
                    )
                    onConfirmation(updated)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Dismiss")
            }
        }
    )
}

@Composable
fun SpaceCheckboxCard(
    icon: ImageVector,
    title: String,
    isChecked: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCheckChange(!isChecked) },
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircleWrapper(color = MaterialTheme.colorScheme.primary) {
                    Icon(icon, null, tint = MaterialTheme.colorScheme.onPrimary)
                }
                Text(title, fontWeight = FontWeight.Bold)
            }

            Checkbox(
                checked = isChecked,
                onCheckedChange = onCheckChange
            )
        }
    }
}

@Composable
fun SelectLabelDialog(
    currNoteLabels: List<LabelModel>,
    labels: List<LabelModel>,
    onAddLabel: () -> Unit,
    onConfirmation: (List<LabelModel>)->Unit,
    onDismissRequest: ()-> Unit
){
    val selectedLabel = remember { mutableStateListOf<LabelModel>().apply {
        addAll(currNoteLabels)
    } }

    AlertDialog(
        icon = { CircleWrapper(MaterialTheme.colorScheme.primary) { Icon(Icons.AutoMirrored.Filled.Label, contentDescription = "Label Icon", tint = MaterialTheme.colorScheme.onPrimary) } },
        title = { Text(text = "Select Label") },
        text = { LabelCheckBoxList(selectedLabel, labels, onChecked = {selectedLabel.add(it)}, onAddLabel = {
            onAddLabel()
            onDismissRequest()
        }, onUnChecked = { selectedLabel.remove(it)} )  },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation(selectedLabel)
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Confirm")
            }
        },
        dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text("Dismiss") } }
    )
}

@Composable
fun LabelCheckBoxList(
    checkedLabel: List<LabelModel>,
    labels: List<LabelModel>,
    onChecked: (LabelModel)-> Unit,
    onUnChecked: (LabelModel) -> Unit,
    onAddLabel: ()->Unit
){

    LazyColumn(
        modifier = Modifier.heightIn(max=250.dp)
    ) {
        item {
            Card(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .padding(vertical = 2.dp).clickable(onClick = onAddLabel),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircleWrapper(color = MaterialTheme.colorScheme.primary) {
                            Icon(Icons.Default.NewLabel, null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Text(text = "Add New Label", fontWeight = FontWeight.Bold)
                    }
                    IconButton(onAddLabel) { }
                }
            }
        }
        items(labels){ label->
            val isChecked=checkedLabel.contains(label)
            Card(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .padding(vertical = 2.dp).clickable {
                        if (isChecked) {
                            onUnChecked(label)
                        } else {
                            onChecked(label)
                        }
                    },
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    Modifier.padding(vertical = 6.dp, horizontal = 8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircleWrapper(color = MaterialTheme.colorScheme.primary) {
                            Icon(Icons.AutoMirrored.Outlined.LabelImportant, null, tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        Text(text = label.value, fontWeight = FontWeight.Bold, overflow = TextOverflow.Ellipsis, maxLines = 1, modifier = Modifier.width(150.dp))
                    }
                    Checkbox(checked = isChecked, onCheckedChange = {
                        if(isChecked) { onUnChecked(label) }
                        else { onChecked(label) }
                    })
                }
            }
        }
    }
}