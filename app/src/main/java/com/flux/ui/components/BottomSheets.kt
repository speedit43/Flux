package com.flux.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmAdd
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.flux.R
import com.flux.data.model.HabitModel
import com.flux.data.model.WorkspaceModel
import com.flux.other.icons
import com.flux.other.workspaceIconList
import com.flux.ui.screens.events.toFormattedTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitBottomSheet(
    isEditing: Boolean = false,
    habit: HabitModel? = null,
    isVisible: Boolean,
    sheetState: SheetState,
    onConfirm: (HabitModel, Long) -> Unit,
    onDismissRequest: () -> Unit,
) {
    if (!isVisible) return
    var newHabitTitle by remember { mutableStateOf(habit?.title ?: "") }
    var newHabitDescription by remember { mutableStateOf(habit?.description ?: "") }
    var newHabitTime by remember {
        mutableLongStateOf(
            habit?.startDateTime ?: System.currentTimeMillis()
        )
    }
    var timePickerDialog by remember { mutableStateOf(false) }
    val focusRequesterDesc = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        sheetMaxWidth = 500.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    if (isEditing) Icons.Default.Edit else Icons.Default.Add,
                    null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    if (isEditing) stringResource(R.string.Edit_Habit) else stringResource(R.string.Add_Habit),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Spacer(Modifier.height(4.dp))

            TextField(
                value = newHabitTitle,
                onValueChange = { newHabitTitle = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.Title)) },
                singleLine = true,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusRequesterDesc.requestFocus() })
            )

            TextField(
                value = newHabitDescription,
                onValueChange = { newHabitDescription = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequesterDesc),
                placeholder = { Text(stringResource(R.string.Description)) },
                singleLine = true,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.clearFocus(force = true)
                        keyboardController?.hide()
                        timePickerDialog = true
                    }
                )
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AlarmAdd,
                        contentDescription = "Alarm Icon"
                    )

                    Text(
                        text = newHabitTime.toFormattedTime(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                FilledTonalIconButton(
                    onClick = { timePickerDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Pick Time"
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = {
                    keyboardController?.hide()
                    onDismissRequest()
                }) { Text(stringResource(R.string.Dismiss)) }

                Spacer(Modifier.width(8.dp))

                FilledTonalButton(
                    onClick = {
                        if (habit == null) {
                            val newHabit = HabitModel(
                                title = newHabitTitle,
                                startDateTime = newHabitTime,
                                description = newHabitDescription
                            )
                            onConfirm(newHabit, getAdjustedTime(newHabitTime))
                        } else {
                            onConfirm(
                                habit.copy(
                                    title = newHabitTitle,
                                    description = newHabitDescription,
                                    startDateTime = newHabitTime
                                ), getAdjustedTime(newHabitTime)
                            )
                        }

                        keyboardController?.hide()
                        onDismissRequest()
                    }, enabled = newHabitTitle.isNotBlank()
                ) { Text(stringResource(R.string.Confirm)) }
            }

            if (timePickerDialog) {
                TimePicker(
                    initialTime = newHabitTime,
                    onConfirm = {
                        val habitCalendar = Calendar.getInstance().apply {
                            timeInMillis = newHabitTime
                            set(Calendar.HOUR_OF_DAY, it.hour)
                            set(Calendar.MINUTE, it.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        newHabitTime = habitCalendar.timeInMillis
                    }
                ) { timePickerDialog = false }
            }
        }
    }
}

fun getAdjustedTime(time: Long): Long {
    val now = Calendar.getInstance()
    val habitCalendar = Calendar.getInstance().apply { timeInMillis = time }
    val adjustedCalendar = Calendar.getInstance().apply {
        timeInMillis = habitCalendar.timeInMillis
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // If the time has already passed today, schedule for tomorrow
        if (before(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    return adjustedCalendar.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewWorkspaceBottomSheet(
    isEditing: Boolean = false,
    workspace: WorkspaceModel = WorkspaceModel(),
    isVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (WorkspaceModel) -> Unit
) {
    var title by remember { mutableStateOf(workspace.title) }
    var description by remember { mutableStateOf(workspace.description) }
    val focusRequesterDesc = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                keyboardController?.hide()
                onDismiss()
                title = workspace.title
                description = workspace.description
            },
            sheetState = sheetState,
            sheetMaxWidth = 500.dp,
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(if (isEditing) Icons.Default.Edit else Icons.Default.Add, null)
                    Text(
                        if (isEditing) stringResource(R.string.Edit_Workspace) else stringResource(R.string.Add_Workspace),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 3.dp),
                    placeholder = { Text(stringResource(R.string.Title)) },
                    singleLine = true,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterDesc),
                    placeholder = { Text(stringResource(R.string.Description)) },
                    singleLine = true,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedPlaceholderColor = MaterialTheme.colorScheme.primary
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            focusManager.clearFocus(force = true)
                            onConfirm(workspace.copy(title = title, description = description))
                            onDismiss()
                            title = workspace.title
                            description = workspace.description
                        }
                    )
                )

                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = {
                        keyboardController?.hide()
                        onDismiss()
                        title = workspace.title
                        description = workspace.description
                    }) {
                        Text(stringResource(R.string.Dismiss))
                    }

                    Spacer(Modifier.width(8.dp))

                    FilledTonalButton(
                        enabled = title.isNotBlank(),
                        onClick = {
                            keyboardController?.hide()
                            onConfirm(workspace.copy(title = title, description = description))
                            onDismiss()
                            title = workspace.title
                            description = workspace.description
                        }
                    ) {
                        Text(stringResource(R.string.Confirm))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeIconBottomSheet(
    isVisible: Boolean,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
            ) {
                items(workspaceIconList) { item ->
                    Text(
                        item.title,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    FlowRow(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        item.icons.forEach { index ->
                            IconButton({ onConfirm(index) }) { Icon(icons[index], null) }
                        }
                    }
                }
            }
        }
    }
}