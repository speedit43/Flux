package com.flux.ui.screens.journal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.flux.R
import com.flux.data.model.JournalModel
import com.flux.navigation.NavRoutes
import com.flux.ui.events.JournalEvents
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalHome(
    navController: NavController,
    isLoadingMore: Boolean,
    workspaceId: Long,
    allEntries: List<JournalModel>,
    onJournalEvents: (JournalEvents) -> Unit
) {
    val listState = rememberLazyListState()

    // Grouping entries by Month and Year
    val grouped = remember(allEntries) {
        allEntries.sortedBy { it.dateTime }.groupBy {
            val date =
                Instant.ofEpochMilli(it.dateTime).atZone(ZoneId.systemDefault()).toLocalDate()
            Pair(date.month, date.year)
        }
    }

    if (allEntries.isEmpty()) {
        val currentMonth = LocalDate.now().month
        val currentYear = LocalDate.now().year
        Text(
            text = "$currentMonth, $currentYear",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )
            Text(stringResource(R.string.Empty_Journal))
        }
    }

    PullToRefreshBox(
        isRefreshing = isLoadingMore,
        onRefresh = { onJournalEvents(JournalEvents.LoadPreviousMonthEntries(workspaceId)) },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            state = listState
        ) {
            // Loading indicator at the top
            if (isLoadingMore) {
                item(key = "loading") {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }

            // Grouped entries
            grouped.forEach { (monthYear, entries) ->
                val monthName = monthYear.first.getDisplayName(TextStyle.FULL, Locale.getDefault())
                val year = monthYear.second
                val headerKey = "header-${monthYear.first}-${monthYear.second}"

                item(key = headerKey) {
                    Text(
                        text = "$monthName, $year",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                itemsIndexed(entries, key = { _, entry -> entry.journalId }) { index, entry ->
                    JournalPreview(entry, index == entries.lastIndex) {
                        navController.navigate(
                            NavRoutes.EditJournal.withArgs(
                                workspaceId,
                                entry.journalId
                            )
                        )
                    }
                }
            }

            // Bottom spacer to keep last entry from sticking to the edge
            item(key = "bottom_spacer") {
                Spacer(modifier = Modifier.height(300.dp)) // Adjust height as needed
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalPreview(journalEntry: JournalModel, isLast: Boolean = false, onClick: () -> Unit) {
    val dateTime =
        Instant.ofEpochMilli(journalEntry.dateTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
    val dayOfWeek = dateTime.dayOfWeek.name.take(3)
    val dayOfMonth = dateTime.dayOfMonth.toString()
    val timeFormatted = dateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    val richTextState = rememberRichTextState()
    val scrollState = rememberScrollState()

    LaunchedEffect(journalEntry.text) {
        richTextState.setHtml(journalEntry.text)
        scrollState.scrollTo(0)
    }

    Column(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }) {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                Modifier.padding(start = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    dayOfWeek,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    dayOfMonth,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 100.dp)
                            .verticalScroll(scrollState)
                    ) {
                        RichTextEditor(
                            state = richTextState,
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraLight),
                            colors = RichTextEditorDefaults.richTextEditorColors(
                                disabledIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                disabledTextColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                    Row(
                        Modifier.padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(12.dp))
                        Text(
                            timeFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraLight),
                            modifier = Modifier.alpha(0.8f)
                        )
                    }
                }

                if (journalEntry.images.isNotEmpty()) {
                    AsyncImage(
                        model = journalEntry.images.first(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        if (!isLast) {
            HorizontalDivider()
        } else Spacer(Modifier.height(8.dp))
    }
}
