package com.flux.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.flux.data.model.WorkspaceModel

@Composable
fun WorkSpacesCard(
    shape: Shape= RoundedCornerShape(16.dp),
    icon: ImageVector=Icons.Default.Workspaces,
    workspace: WorkspaceModel,
    isSelected: Boolean=false,
    onClick: ()->Unit,
    onLongPress: (Int)->Unit
){
    ElevatedCard(
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)),
        shape = shape,
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .height(150.dp)
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onLongPress(workspace.workspaceId) }
            )
            .border(width = if (isSelected) 1.dp else 0.dp, color = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh, shape = shape),
    ) {
        Box(Modifier.fillMaxSize().padding(16.dp)){
            Icon(icon, null, modifier = Modifier.align(Alignment.TopStart))
            Icon(Icons.Default.Lock, null, modifier = Modifier.align(Alignment.TopEnd))

            Column(modifier = Modifier.fillMaxWidth().padding(top = 36.dp)) {
                Text(workspace.title, fontWeight = FontWeight.Bold, modifier = Modifier.alpha(0.90f), style = MaterialTheme.typography.titleLarge)
                Text(workspace.description, modifier = Modifier.alpha(0.85f), maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWorkspaceBottomSheet(
    isVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (WorkspaceModel) -> Unit
){
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val focusRequesterDesc = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                keyboardController?.hide()
                onDismiss()
                title=""
                description=""
            },
            sheetState = sheetState
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp).imePadding(), horizontalAlignment = Alignment.CenterHorizontally) {
                RenderCategoryIcon(Icons.Default.Workspaces)
                Spacer(Modifier.height(8.dp))
                Text("Add Workspace", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = title,
                    onValueChange = { title=it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 3.dp),
                    placeholder = {Text("Title")},
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(onNext = { focusRequesterDesc.requestFocus() })
                )

                TextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterDesc),
                    placeholder = { Text("Description") },
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            onConfirm(WorkspaceModel(title=title, description = description))
                            onDismiss()
                            title=""
                            description=""
                        }
                    )
                )

                Row(
                    Modifier.align(Alignment.End).padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedButton(onClick ={
                        keyboardController?.hide()
                        onDismiss()
                        title=""
                        description=""
                    } ) { Text("Dismiss") }
                    FilledTonalButton(onClick = {
                        keyboardController?.hide()
                        onConfirm(WorkspaceModel(title=title, description = description))
                        onDismiss()
                        title=""
                        description=""
                    }) { Text("Confirm") }
                }
            }
        }
    }
}