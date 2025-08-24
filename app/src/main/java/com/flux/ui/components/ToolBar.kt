package com.flux.ui.components

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.ViewStream
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.flux.R
import com.flux.navigation.NavRoutes
import com.flux.other.canScheduleReminder
import com.flux.other.isNotificationPermissionGranted
import com.flux.other.openAppNotificationSettings
import com.flux.other.requestExactAlarmPermission

@Composable
fun SpacesToolBar(
    title: String,
    icon: ImageVector,
    onMainClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
    ) {
        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onMainClick() }
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = "space",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "space",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            VerticalDivider(Modifier.fillMaxHeight())

            // Right section (Edit icon)
            Row(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable { onEditClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Edit,
                    modifier = Modifier.padding(4.dp),
                    contentDescription = "Edit-space",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun TodoToolBar(navController: NavController, workspaceId: Long) {
    IconButton({navController.navigate(NavRoutes.TodoDetail.withArgs(workspaceId, -1))}) {
        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun JournalToolBar(navController: NavController, workspaceId: Long) {
    IconButton({navController.navigate(NavRoutes.EditJournal.withArgs(workspaceId, -1))}) {
        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun CalenderToolBar(isMonthlyView: Boolean, onClick: (Boolean) -> Unit) {
    IconButton({onClick(!isMonthlyView)}) {
        Icon(if(isMonthlyView) Icons.Default.CalendarViewDay else Icons.Default.CalendarViewMonth, null, tint = MaterialTheme.colorScheme.primary)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU, Build.VERSION_CODES.S)
@Composable
fun HabitToolBar(context: Context, onAddClick: ()->Unit) {
    IconButton({
        if(!canScheduleReminder(context)){
            Toast.makeText(context, context.getText(R.string.Reminder_Permission), Toast.LENGTH_SHORT).show()
            requestExactAlarmPermission(context)
        }
        if (!isNotificationPermissionGranted(context)) {
            Toast.makeText(context, context.getText(R.string.Notification_Permission), Toast.LENGTH_SHORT).show()
            openAppNotificationSettings(context)
        }
        if (canScheduleReminder(context) && isNotificationPermissionGranted(context)) { onAddClick() }
    }) { Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary) }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU, Build.VERSION_CODES.S)
@Composable
fun EventToolBar(context: Context, navController: NavController, workspaceId: Long) {
    IconButton({
        if(!canScheduleReminder(context)) {
            Toast.makeText(context, context.getText(R.string.Reminder_Permission), Toast.LENGTH_SHORT).show()
            requestExactAlarmPermission(context)
        }
        if(!isNotificationPermissionGranted(context)) {
            Toast.makeText(context, context.getText(R.string.Notification_Permission), Toast.LENGTH_SHORT).show()
            openAppNotificationSettings(context)
        }
        if(canScheduleReminder(context) && isNotificationPermissionGranted(context)){
            navController.navigate(NavRoutes.EventDetails.withArgs(workspaceId, -1))
        }
    }) { Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary) }
}

@Composable
fun NotesToolBar(navController: NavController, workspaceId: Long, query: String, isGridView: Boolean, onChangeView: ()->Unit, onSearch: (String)->Unit) {
    var onSearchClicked by remember { mutableStateOf(false) }

    if (!onSearchClicked) {
        Row {
            IconButton({ onSearchClicked = true }) {
                Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onChangeView) {
                val icon = when {
                    isGridView -> Icons.Default.ViewStream
                    else -> Icons.Default.GridView
                }
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            }
            IconButton({ navController.navigate(NavRoutes.NoteDetails.withArgs(workspaceId, -1)) }) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    } else {
        NotesSearchBar(
            query = query,
            onQueryChange = { onSearch(it) },
            onCloseClicked = { onSearchClicked = false },
            modifier = Modifier.width(200.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectedNotesToolBar(
    isAllSelected: Boolean,
    isAllSelectedPinned: Boolean,
    selectedNotes: List<Long>,
    onPinClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onCloseClick) { Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.primary) }
            Text("${selectedNotes.size}", color = MaterialTheme.colorScheme.primary)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(onClick = onPinClick) { Icon(if(isAllSelectedPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin, null, tint = MaterialTheme.colorScheme.primary)  }
            IconButton(onSelectAllClick) { Icon(if(isAllSelected) Icons.Default.Deselect else Icons.Default.SelectAll, null, tint = MaterialTheme.colorScheme.primary) }
            IconButton(onDeleteClick) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Composable
fun NotesSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseClicked: ()->Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search..."
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(6.dp))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp), // keeps text centered vertically
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    innerTextField()
                }
            )

            IconButton(
                onClick = {
                    onCloseClicked()
                    onQueryChange("") },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}