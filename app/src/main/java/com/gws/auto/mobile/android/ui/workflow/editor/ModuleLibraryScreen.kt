package com.gws.auto.mobile.android.ui.workflow.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.layout.Arrangement
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.cos
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.domain.model.Module
import com.gws.auto.mobile.android.domain.model.ModuleCatalog
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


private val ITEM_HEIGHT = 56.dp

@Composable
fun ModuleLibraryScreen(
    onModuleSelected: (Module) -> Unit,
    onDismiss: () -> Unit
) {
    val folders = ModuleCatalog.folders
    var selectedFolderIndex by remember { mutableStateOf(0) }
    val currentModules by remember(selectedFolderIndex) {
        derivedStateOf { folders.getOrNull(selectedFolderIndex)?.modules ?: emptyList() }
    }
    
    // Infinite list simulation
    val infiniteFactor = 1000
    val initialFolderIndex = (infiniteFactor / 2) * folders.size
    val folderListState = rememberLazyListState(initialFirstVisibleItemIndex = initialFolderIndex)
    
    // Track selected module
    var selectedModuleIndex by remember { mutableStateOf(0) }
    val initialModuleIndex = if (currentModules.isNotEmpty()) (infiniteFactor / 2) * currentModules.size else 0
    val moduleListState = rememberLazyListState(initialFirstVisibleItemIndex = initialModuleIndex)

    // Track reel height for consistent centering
    var reelHeightPx by remember { mutableStateOf(0f) }

    // Update selected folder based on scroll
    LaunchedEffect(folderListState, reelHeightPx) {
        snapshotFlow {
            val layoutInfo = folderListState.layoutInfo
            // Use reported reelHeightPx if available (and valid), otherwise fallback to viewportEndOffset
            val height = if (reelHeightPx > 0) reelHeightPx else layoutInfo.viewportEndOffset.toFloat()
            val viewportCenter = height / 2f
            
            val closestItem = layoutInfo.visibleItemsInfo.minByOrNull { 
                val itemCenter = it.offset + it.size / 2
                abs(itemCenter - viewportCenter) 
            }
            
            if (closestItem != null) {
                // Empirical correction: User reports sign was inverted.
                // Original state was +2 (Logic > Visual).
                // So we subtract 2 to correct it.
                closestItem.index - 2
            } else {
                null
            }
        }
            .distinctUntilChanged()
            .collect { centeredIndex ->
                if (centeredIndex != null) {
                    // Handle potential negative indices with safe modulo
                    val actualIndex = (centeredIndex % folders.size + folders.size) % folders.size
                    if (selectedFolderIndex != actualIndex) {
                        selectedFolderIndex = actualIndex
                        // Reset module list when folder changes
                        if (currentModules.isNotEmpty()) {
                            val newInitial = (infiniteFactor / 2) * currentModules.size
                            moduleListState.scrollToItem(newInitial)
                        }
                    }
                }
            }
    }

    // Update selected module based on scroll
    LaunchedEffect(moduleListState, currentModules, reelHeightPx) {
        snapshotFlow {
            val layoutInfo = moduleListState.layoutInfo
            val height = if (reelHeightPx > 0) reelHeightPx else layoutInfo.viewportEndOffset.toFloat()
            val viewportCenter = height / 2f
            layoutInfo.visibleItemsInfo.minByOrNull { 
                abs((it.offset + it.size / 2) - viewportCenter) 
            }?.index?.minus(2) // Apply same -2 offset for consistency
        }
            .distinctUntilChanged()
            .collect { centeredIndex ->
                if (centeredIndex != null && currentModules.isNotEmpty()) {
                    // Handle potential negative indices with safe modulo
                    selectedModuleIndex = (centeredIndex % currentModules.size + currentModules.size) % currentModules.size
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp) // Increased height to accommodate fixed reel size
            .background(MaterialTheme.colorScheme.surface)
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = folders.getOrNull(selectedFolderIndex)?.name ?: "Select Module",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Folder Reel
            Box(modifier = Modifier
                .weight(1f)
                .height(ITEM_HEIGHT * 5)
            ) {
                ReelList(
                    itemCount = folders.size * infiniteFactor,
                    state = folderListState,
                    itemHeight = ITEM_HEIGHT,
                    onHeightChanged = { reelHeightPx = it }
                ) { index ->
                    val folder = folders[index % folders.size]
                    ReelItem(
                        text = folder.name,
                        iconRes = R.drawable.ic_folder,
                        isSelected = (index % folders.size) == selectedFolderIndex
                    )
                }
                
                // Selection Indicator Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )
            }

            Divider(
                modifier = Modifier
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Module Reel
            Box(modifier = Modifier
                .weight(1f)
                .height(ITEM_HEIGHT * 5)
            ) {
                if (currentModules.isNotEmpty()) {
                    ReelList(
                        itemCount = currentModules.size * infiniteFactor,
                        state = moduleListState,
                        itemHeight = ITEM_HEIGHT,
                        onHeightChanged = { /* Shared height */ }
                    ) { index ->
                        val module = currentModules[index % currentModules.size]
                        val context = LocalContext.current
                        val moduleKeys = context.resources.getStringArray(R.array.module_keys)
                        val moduleDisplayNames = context.resources.getStringArray(R.array.module_display_names)
                        
                        val nameIndex = moduleKeys.indexOf(module.type)
                        val displayName = if (nameIndex != -1 && nameIndex < moduleDisplayNames.size) {
                            moduleDisplayNames[nameIndex]
                        } else {
                            module.type
                        }
                        
                        ReelItem(
                            text = displayName,
                            iconRes = R.drawable.ic_module,
                            isSelected = (index % currentModules.size) == selectedModuleIndex
                        )
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No modules", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                
                // Selection Indicator Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )
            }
        }

        Button(
            onClick = {
                if (currentModules.isNotEmpty()) {
                    onModuleSelected(currentModules[selectedModuleIndex])
                    onDismiss()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Add Module")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelList(
    itemCount: Int,
    state: LazyListState,
    itemHeight: androidx.compose.ui.unit.Dp,
    onHeightChanged: (Float) -> Unit,
    content: @Composable (Int) -> Unit
) {
    val scope = rememberCoroutineScope()
    
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val height = maxHeight
        val verticalPadding = (height - itemHeight) / 2
        
        val density = LocalDensity.current
        LaunchedEffect(height) {
            with(density) {
                onHeightChanged(height.toPx())
            }
        }
        
        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = verticalPadding),
            flingBehavior = rememberSnapFlingBehavior(
                snapLayoutInfoProvider = remember(state) {
                    SnapLayoutInfoProvider(
                        lazyListState = state,
                        snapPosition = SnapPosition.Center
                    )
                }
            )
        ) {
            items(itemCount) { index ->
                // Calculate 3D effects
                // Use heightPx for viewportCenter to ensure consistency with the padding calculation
                val density = LocalDensity.current
                val heightPx = with(density) { height.toPx() }

                val viewportCenter = heightPx / 2f
                
                val itemInfo = state.layoutInfo.visibleItemsInfo.find { it.index == index }
                
                var rotationX = 0f
                var scale = 1f
                var normalizedDistance = 0f


                if (itemInfo != null) {
                    val verticalPaddingPx = with(density) { verticalPadding.toPx() }
                    val itemCenter = itemInfo.offset + itemInfo.size / 2f
                    val distanceFromCenter = itemCenter - viewportCenter
                    // Simplified "Magnified Center" effect
                    // Instead of 3D cylinder projection which causes layout mismatches,
                    // we simply scale down and fade out items as they move away from the center.
                    
                    normalizedDistance = (distanceFromCenter / viewportCenter).coerceIn(-1f, 1f)
                    
                    // Scale: Center is 1.0f, Edges are 0.8f
                    // Use a slightly sharper curve to emphasize the center
                    scale = 1f - kotlin.math.abs(normalizedDistance) * 0.3f
                    
                    // Alpha: Center is 1.0f, Edges are 0.4f
                    val alpha = 1f - kotlin.math.abs(normalizedDistance) * 0.6f
                    
                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth()
                            .graphicsLayer {
                                this.scaleX = scale
                                this.scaleY = scale
                                this.alpha = alpha
                            }
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = LocalIndication.current
                            ) {
                                scope.launch {
                                    state.animateScrollToItem(index)
                                }
                            }
                    ) {
                        content(index)
                    }
                } else {
                     // Fallback for items not visible (shouldn't happen often in this loop structure but good for safety)
                     Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .fillMaxWidth()
                     )
                }
            }
        }
    }
}

@Composable
fun ReelItem(
    text: String,
    iconRes: Int,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
