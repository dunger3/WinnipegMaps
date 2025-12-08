package com.example.appdemo2.screens.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appdemo2.data.models.FavoriteLocation
import com.google.android.gms.maps.model.LatLng

///**
// * Manages all map-related dialogs such as adding favorites, creating alerts, renaming, and routing.
// */
@Composable
fun MapDialogsSection(
    canEditFavorites: Boolean,
    showAddDialog: Boolean,
    onDismissAddDialog: () -> Unit,
    onConfirmAddFavorite: () -> Unit,
    selectedLocation: FavoriteLocation?,
    editedName: String,
    onEditedNameChange: (String) -> Unit,
    onRenameDismiss: () -> Unit,
    onRenameSave: (FavoriteLocation, String) -> Unit,
    canCreateAlerts: Boolean,
    showAddAlertDialog: Boolean,
    pendingAlertLocation: LatLng?,
    alertTitle: String,
    alertDescription: String,
    onAlertTitleChange: (String) -> Unit,
    onAlertDescriptionChange: (String) -> Unit,
    onAlertDismiss: () -> Unit,
    onAlertSave: (LatLng, String, String) -> Unit,
    favorites: List<FavoriteLocation>,
    showDirectionDialog: Boolean,
    startFavorite: FavoriteLocation?,
    endFavorite: FavoriteLocation?,
    onStartFavoriteChange: (FavoriteLocation) -> Unit,
    onEndFavoriteChange: (FavoriteLocation) -> Unit,
    onDirectionDialogDismiss: () -> Unit,
    onDrawRoute: () -> Unit,
    onStartPlacingAlert: (String, String) -> Unit,
    centerAddress: String?
) {
    if (selectedLocation != null && canEditFavorites) {
        AlertDialog(
            onDismissRequest = onRenameDismiss,
            title = { Text("Rename Favorite") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = onEditedNameChange,
                        label = { Text("Favorite Name") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Address: ${centerAddress ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onRenameSave(selectedLocation, editedName)
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onRenameDismiss) { Text("Cancel") }
            }
        )
    }

    if (showAddDialog && canEditFavorites) {
        AlertDialog(
            onDismissRequest = onDismissAddDialog,
            title = { Text("Add Favorite") },
            text = { Text("Next map tap will add a new favorite.") },
            confirmButton = {
                TextButton(onClick = onConfirmAddFavorite) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = onDismissAddDialog) { Text("Cancel") }
            }
        )
    }

    if (showAddAlertDialog && canCreateAlerts) {
        AlertDialog(
            onDismissRequest = onAlertDismiss,
            title = { Text("Add Alert") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Create a community alert.")
                    OutlinedTextField(
                        value = alertTitle,
                        onValueChange = onAlertTitleChange,
                        label = { Text("Title") }
                    )
                    OutlinedTextField(
                        value = alertDescription,
                        onValueChange = onAlertDescriptionChange,
                        label = { Text("Description") },
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (pendingAlertLocation != null) {
                        onAlertSave(
                            pendingAlertLocation,
                            alertTitle,
                            alertDescription
                        )
                    } else {
                        onStartPlacingAlert(alertTitle, alertDescription)
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onAlertDismiss) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDirectionDialog && canEditFavorites) {
        DirectionDialog(
            favorites = favorites,
            startFavorite = startFavorite,
            endFavorite = endFavorite,
            onStartSelect = onStartFavoriteChange,
            onEndSelect = onEndFavoriteChange,
            onCancel = onDirectionDialogDismiss,
            onDrawRoute = onDrawRoute
        )
    }
}

//**
// * Dialog for selecting start and end favorites before drawing a route.
// */
@Composable
fun DirectionDialog(
    favorites: List<FavoriteLocation>,
    startFavorite: FavoriteLocation?,
    endFavorite: FavoriteLocation?,
    onStartSelect: (FavoriteLocation) -> Unit,
    onEndSelect: (FavoriteLocation) -> Unit,
    onCancel: () -> Unit,
    onDrawRoute: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Select Route") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Start Location:")
                FavoriteDropdown(favorites, startFavorite, onStartSelect)

                Spacer(Modifier.height(8.dp))

                Text("End Location:")
                FavoriteDropdown(favorites, endFavorite, onEndSelect)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDrawRoute()
                    onCancel()
                }
            ) {
                Text("Draw Route")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    )
}

//**
// * Dropdown component used for selecting a favorite location from a list.
// */
@Composable
fun FavoriteDropdown(
    items: List<FavoriteLocation>,
    selected: FavoriteLocation?,
    onSelect: (FavoriteLocation) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selected?.name ?: "Select")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.name) },
                    onClick = {
                        expanded = false
                        onSelect(item)
                    }
                )
            }
        }
    }
}
