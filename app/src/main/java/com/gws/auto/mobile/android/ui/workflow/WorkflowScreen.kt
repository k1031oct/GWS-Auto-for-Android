package com.gws.auto.mobile.android.ui.workflow

import android.content.ClipData
import android.content.ClipDescription
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.DragAndDropSourceScope
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.domain.model.Workflow
import com.gws.auto.mobile.android.domain.model.WorkflowFolder
import com.gws.auto.mobile.android.domain.model.WorkflowListItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WorkflowScreen(
    workflowItems: List<WorkflowListItem>,
    onRunClicked: (Workflow) -> Unit,
    onEditClicked: (Workflow) -> Unit,
    onDeleteClicked: (Workflow) -> Unit,
    onFolderDeleteClicked: (WorkflowFolder) -> Unit,
    onAddClicked: () -> Unit,
    onFavoriteClicked: (Workflow) -> Unit,
    onFolderClicked: (WorkflowFolder) -> Unit,
    onMoveWorkflowToFolder: (String, String) -> Unit,
    fabClickFlow: kotlinx.coroutines.flow.Flow<Unit>,
    onCreateFolder: (String) -> Unit
) {
    var showDeleteWorkflowDialog by remember { mutableStateOf<Workflow?>(null) }
    var showDeleteFolderDialog by remember { mutableStateOf<WorkflowFolder?>(null) }
    var showCreateFolderDialog by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fabClickFlow.collect {
            showCreateFolderDialog = true
        }
    }

    if (showDeleteWorkflowDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteWorkflowDialog = null },
            title = { Text(showDeleteWorkflowDialog!!.name) },
            text = { Text("このワークフローを本当に削除しますか？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClicked(showDeleteWorkflowDialog!!)
                        showDeleteWorkflowDialog = null
                    }
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWorkflowDialog = null }) {
                    Text("キャンセル")
                }
            }
        )
    }

    if (showDeleteFolderDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteFolderDialog = null },
            title = { Text(showDeleteFolderDialog!!.name) },
            text = { Text("このフォルダを本当に削除しますか？ (中のワークフローは削除されません)") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onFolderDeleteClicked(showDeleteFolderDialog!!)
                        showDeleteFolderDialog = null
                    }
                ) {
                    Text("削除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteFolderDialog = null }) {
                    Text("キャンセル")
                }
            }
        )
    }

    if (showCreateFolderDialog) {
        AlertDialog(
            onDismissRequest = { showCreateFolderDialog = false },
            title = { Text("Create Folder") },
            text = {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("Folder Name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newFolderName.isNotBlank()) {
                            onCreateFolder(newFolderName)
                            newFolderName = ""
                        }
                        showCreateFolderDialog = false
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateFolderDialog = false
                        newFolderName = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(workflowItems) { item ->
            when (item) {
                is WorkflowListItem.WorkflowItem -> {
                    DraggableWorkflowItemRow(
                        item = item,
                        onRunClicked = onRunClicked,
                        onEditClicked = onEditClicked,
                        onDeleteClicked = { showDeleteWorkflowDialog = item.workflow },
                        onFavoriteClicked = onFavoriteClicked
                    )
                }
                is WorkflowListItem.FolderItem -> {
                    DropTargetFolderItemRow(
                        item = item,
                        onFolderClicked = onFolderClicked,
                        onFolderDeleteClicked = { showDeleteFolderDialog = item.folder },
                        onDrop = { workflowId ->
                            onMoveWorkflowToFolder(workflowId, item.folder.id)
                        }
                    )
                }
                is WorkflowListItem.AddItem -> {
                    // Add root drop zone before AddItem
                    RootDropZoneRow(
                        onDrop = { workflowId ->
                            onMoveWorkflowToFolder(workflowId, "")
                        }
                    )
                    AddItemRow(onAddClicked = onAddClicked)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DraggableWorkflowItemRow(
    item: WorkflowListItem.WorkflowItem,
    onRunClicked: (Workflow) -> Unit,
    onEditClicked: (Workflow) -> Unit,
    onDeleteClicked: (Workflow) -> Unit,
    onFavoriteClicked: (Workflow) -> Unit
) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .dragAndDropSource(
                block = {
                    detectTapGestures(
                        onLongPress = { offset ->
                            startTransfer(
                                DragAndDropTransferData(
                                    clipData = ClipData.newPlainText("workflowId", item.workflow.id),
                                    flags = android.view.View.DRAG_FLAG_GLOBAL
                                )
                            )
                        }
                    )
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = { onEditClicked(item.workflow) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.workflow.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.workflow.description.isNotBlank()) {
                    Text(
                        text = item.workflow.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            IconButton(onClick = { onRunClicked(item.workflow) }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Run")
            }
            
            var showMenu by remember { mutableStateOf(false) }
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Options")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(if (item.workflow.isFavorite) "Unfavorite" else "Favorite") },
                        onClick = {
                            showMenu = false
                            onFavoriteClicked(item.workflow)
                        },
                        leadingIcon = {
                            Icon(
                                if (item.workflow.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (item.workflow.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            showMenu = false
                            onEditClicked(item.workflow)
                        },
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                            onDeleteClicked(item.workflow)
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DropTargetFolderItemRow(
    item: WorkflowListItem.FolderItem,
    onFolderClicked: (WorkflowFolder) -> Unit,
    onFolderDeleteClicked: (WorkflowFolder) -> Unit,
    onDrop: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var isDraggingOver by remember { mutableStateOf(false) }

    val dropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                isDraggingOver = false
                val clipData = event.toAndroidDragEvent().clipData
                if (clipData != null && clipData.itemCount > 0) {
                    val workflowId = clipData.getItemAt(0).text.toString()
                    onDrop(workflowId)
                    return true
                }
                return false
            }

            override fun onEntered(event: DragAndDropEvent) {
                isDraggingOver = true
            }

            override fun onExited(event: DragAndDropEvent) {
                isDraggingOver = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = dropTarget
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = { onFolderClicked(item.folder) }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDraggingOver) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (item.isExpanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (item.isExpanded) "Collapse" else "Expand",
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = item.folder.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            
            // Folder Menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Folder Options")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete Folder") },
                        onClick = {
                            showMenu = false
                            onFolderDeleteClicked(item.folder)
                        },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RootDropZoneRow(
    onDrop: (String) -> Unit
) {
    var isDraggingOver by remember { mutableStateOf(false) }

    val dropTarget = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                isDraggingOver = false
                val clipData = event.toAndroidDragEvent().clipData
                if (clipData != null && clipData.itemCount > 0) {
                    val workflowId = clipData.getItemAt(0).text.toString()
                    onDrop(workflowId)
                    return true
                }
                return false
            }

            override fun onEntered(event: DragAndDropEvent) {
                isDraggingOver = true
            }

            override fun onExited(event: DragAndDropEvent) {
                isDraggingOver = false
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = dropTarget
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDraggingOver)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDraggingOver) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Root Level",
                modifier = Modifier.padding(end = 12.dp),
                tint = if (isDraggingOver) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "ルートレベル（フォルダから取り出す）",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDraggingOver) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (isDraggingOver) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Drop here",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun AddItemRow(onAddClicked: () -> Unit) {
    OutlinedButton(
        onClick = onAddClicked,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Add New Workflow")
    }
}
