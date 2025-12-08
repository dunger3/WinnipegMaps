package com.example.appdemo2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appdemo2.data.AlertsRepository
import com.example.appdemo2.data.models.UserAlert
import kotlinx.coroutines.launch

///**
// * Displays a list of community alerts created by users on the map.
// */
@Composable
fun AlertsScreen(
    isLoggedIn: Boolean,
    isGuest: Boolean
) {
    val canDelete = isLoggedIn && !isGuest
    val scope = rememberCoroutineScope()

    val alerts = AlertsRepository.alerts

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Community Alerts",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "These alerts are created by users on the map and are visible to everyone.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        if (alerts.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No alerts yet. Long-press on the map to create one.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(alerts, key = { it.id }) { alert ->
                    AlertCard(
                        alert = alert,
                        canDelete = canDelete,
                        onDelete = {
                            scope.launch {
                                AlertsRepository.deleteAlert(alert.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

///**
// * Displays a single alert entry with optional delete controls.
// */
@Composable
private fun AlertCard(
    alert: UserAlert,
    canDelete: Boolean,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(alert.title, style = MaterialTheme.typography.titleMedium)
                if (canDelete) {
                    TextButton(onClick = onDelete) {
                        Text("Delete")
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            if (alert.description.isNotBlank()) {
                Text(alert.description, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Location: ${"%.5f".format(alert.lat)}, ${"%.5f".format(alert.lng)}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
