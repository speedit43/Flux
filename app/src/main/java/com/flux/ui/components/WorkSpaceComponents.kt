package com.flux.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.flux.R
import com.flux.other.icons

@Composable
fun WorkspaceCard(
    gridColumns: Int,
    radius: Int,
    isLocked: Boolean=false,
    cover: String,
    title: String,
    description: String,
    iconIndex: Int,
    onClick: ()->Unit
){
    val coverHeight = when (gridColumns) {
        1 -> 120.dp
        2 -> 100.dp
        else -> 80.dp
    }

    val maxTitleLines = when (gridColumns) {
        1 -> 2
        else -> 1
    }

    val maxDescriptionLines = when (gridColumns) {
        1 -> 3
        else -> 2
    }

    val paddingValues = when (gridColumns){
        1-> 8.dp
        2-> 6.dp
        else -> 4.dp
    }

    val iconSize = when (gridColumns){
        1-> 28.dp
        2->24.dp
        else -> 18.dp
    }

    val titleStyle =  when (gridColumns){
        1-> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        2-> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        else-> MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
    }

    val descriptionStyle =  when (gridColumns){
        1-> MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Normal)
        2 -> MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Normal)
        else-> MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraLight)
    }

    ElevatedCard(
        shape = shapeManager(radius = radius*2),
        modifier = Modifier.fillMaxWidth().padding(horizontal = paddingValues),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
    ) {
        Column(Modifier.fillMaxSize().padding(bottom = 4.dp)){
            if(cover.isBlank()){ Box(Modifier.fillMaxWidth().height(coverHeight).alpha(0.125f).background(MaterialTheme.colorScheme.onSurface)) }
            else{
                AsyncImage(
                    model = cover.toUri(),
                    modifier = Modifier.height(coverHeight).alpha(0.8f),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Row(modifier = Modifier.padding(vertical = 8.dp, horizontal = paddingValues), verticalAlignment = Alignment.CenterVertically) {
                Icon(icons[iconIndex], null, Modifier.size(iconSize), MaterialTheme.colorScheme.primary)
                if(isLocked) Icon(Icons.Default.Lock, null, Modifier.size(iconSize), MaterialTheme.colorScheme.primary)
                Text(
                    title,
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = maxTitleLines,
                    style = titleStyle,
                    overflow = TextOverflow.Clip,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                description,
                style = descriptionStyle,
                modifier = Modifier.padding(bottom = 6.dp).padding(horizontal = paddingValues),
                maxLines = maxDescriptionLines,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptySpaces(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Workspaces,
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Text(stringResource(R.string.Empty_Workspace))
    }
}