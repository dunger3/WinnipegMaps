package com.example.appdemo2.screens.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appdemo2.data.models.FavoriteLocation

///**
// * Provides the on-map UI controls such as menus, actions, and filters.
// */
@Composable
fun BoxScope.MapControls(
    favorites: List<FavoriteLocation>,
    actionsMenuExpanded: Boolean,
    onActionsMenuExpandedChange: (Boolean) -> Unit,
    selectLocationMenuExpanded: Boolean,
    onSelectLocationMenuExpandedChange: (Boolean) -> Unit,
    onFavoriteSelectedFromMenu: (FavoriteLocation) -> Unit,
    onFavoriteEditFromMenu: (FavoriteLocation) -> Unit,
    onFavoriteDeleteFromMenu: (FavoriteLocation) -> Unit,
    onShowDirectionsClick: () -> Unit,
    onClearTempMarkersClick: () -> Unit,
    onAddFavoriteClick: () -> Unit,
    onAddAlertClick: () -> Unit,
    showGyms: Boolean,
    showGames: Boolean,
    showPizza: Boolean,
    onToggleGyms: () -> Unit,
    onToggleGames: () -> Unit,
    onTogglePizza: () -> Unit,
    onShowCenterInfoClick: () -> Unit
) {
    var addMenuExpanded by remember { mutableStateOf(false) }

    Box(
        Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp)
    ) {
        ElevatedButton(onClick = { onActionsMenuExpandedChange(true) }) {
            Text("Map Options")
        }

        DropdownMenu(
            expanded = actionsMenuExpanded,
            onDismissRequest = { onActionsMenuExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text("Select Location") },
                onClick = {
                    onActionsMenuExpandedChange(false)
                    onSelectLocationMenuExpandedChange(true)
                }
            )
            DropdownMenuItem(
                text = { Text("Directions") },
                onClick = {
                    onActionsMenuExpandedChange(false)
                    onShowDirectionsClick()
                }
            )

        }

        DropdownMenu(
            expanded = selectLocationMenuExpanded,
            onDismissRequest = { onSelectLocationMenuExpandedChange(false) }
        ) {
            favorites.forEach { favorite ->
                DropdownMenuItem(
                    text = {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(favorite.name)
                            Row {
                                IconButton(onClick = {
                                    onFavoriteEditFromMenu(favorite)
                                    onSelectLocationMenuExpandedChange(false)
                                }) {
                                    Icon(Icons.Default.Edit, null)
                                }
                                IconButton(onClick = {
                                    onSelectLocationMenuExpandedChange(false)
                                    onFavoriteDeleteFromMenu(favorite)
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        onSelectLocationMenuExpandedChange(false)
                        onFavoriteSelectedFromMenu(favorite)
                    }
                )
            }
        }
    }

    Box(
        Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
    ) {
        ElevatedButton(onClick = onClearTempMarkersClick) { Text("Clear") }
    }

    Box(
        Modifier
            .align(Alignment.TopEnd)
            .padding(16.dp)
    ) {
        IconButton(onClick = { addMenuExpanded = true }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }

        DropdownMenu(
            expanded = addMenuExpanded,
            onDismissRequest = { addMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Add Favorite") },
                onClick = {
                    addMenuExpanded = false
                    onAddFavoriteClick()
                }
            )
            DropdownMenuItem(
                text = { Text("Add Alert") },
                onClick = {
                    addMenuExpanded = false
                    onAddAlertClick()
                }
            )
        }
    }

    PlacesLegend(
        showGyms = showGyms,
        showGames = showGames,
        showPizza = showPizza,
        onToggleGyms = onToggleGyms,
        onToggleGames = onToggleGames,
        onTogglePizza = onTogglePizza
    )
}

///**
// * Displays filter chips allowing users to toggle place categories on or off.
// */
@Composable
private fun BoxScope.PlacesLegend(
    showGyms: Boolean,
    showGames: Boolean,
    showPizza: Boolean,
    onToggleGyms: () -> Unit,
    onToggleGames: () -> Unit,
    onTogglePizza: () -> Unit
) {
    Surface(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(bottom = 70.dp),
        tonalElevation = 4.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = showGyms,
                onClick = onToggleGyms,
                label = { Text("Gyms") }
            )
            FilterChip(
                selected = showGames,
                onClick = onToggleGames,
                label = { Text("Video Games") }
            )
            FilterChip(
                selected = showPizza,
                onClick = onTogglePizza,
                label = { Text("Pizza") }
            )
        }
    }
}
