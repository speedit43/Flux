package com.flux.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.flux.data.model.LabelModel

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
fun SelectLabelDialog(
    currNoteLabels: List<String>,
    labels: List<LabelModel>,
    onConfirmation: (List<String>)->Unit,
    onDismissRequest: ()-> Unit
){
    val selectedLabel = remember { mutableStateListOf<String>().apply {
        addAll(currNoteLabels)
    } }

    AlertDialog(
        icon = { CircleWrapper(MaterialTheme.colorScheme.primary) { Icon(Icons.AutoMirrored.Filled.Label, contentDescription = "Label Icon", tint = MaterialTheme.colorScheme.onPrimary) } },
        title = { Text(text = "Select Label") },
        text = { LabelCheckBoxList(selectedLabel, labels, onChecked = {selectedLabel.add(it)}) { selectedLabel.remove(it)} },
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
    checkedLabel: List<String>,
    labels: List<LabelModel>,
    onChecked: (String)-> Unit,
    onUnChecked: (String) -> Unit
){

    LazyColumn(
        modifier = Modifier.heightIn(max=250.dp)
    ) {
        items(labels.filter { it.value!="Bookmark" }){ label->
            val isChecked=checkedLabel.contains(label.value)
            Card(
                Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .padding(vertical = 2.dp).clickable {
                        if (isChecked) {
                            onUnChecked(label.value)
                        } else {
                            onChecked(label.value)
                        }
                    },
                shape = RoundedCornerShape(16.dp)
                ) {
                Row(
                    Modifier.padding(8.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label.value)
                    Checkbox(checked = isChecked, onCheckedChange = {
                        if(isChecked) { onUnChecked(label.value) }
                        else { onChecked(label.value) }
                    })
                }
            }
        }
    }
}

@Composable
fun DeleteLabelDialog(
    onConfirmation: ()->Unit,
    onDismissRequest: ()-> Unit
){

    AlertDialog(
        icon = { CircleWrapper(MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Delete, contentDescription = "Delete Icon", tint = MaterialTheme.colorScheme.onPrimary) } },
        title = { Text(text = "Are you sure?") },
        text = { Text(text = "This can't be undone, it will delete label from all notes containing it.")  },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(
                onClick = { onConfirmation()
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary)
            ) { Text("Confirm") }
        },
        dismissButton = { TextButton(onClick = { onDismissRequest() }) { Text("Dismiss") } }
    )
}