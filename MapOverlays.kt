package com.example.appdemo2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

///**
// * Displays an error banner when a route calculation fails.
// */
@Composable
fun RouteErrorBanner(routeError: String?) {
    if (routeError == null) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 72.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = routeError,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

//**
// * Displays a banner showing the resolved address of the map's center position.
// */
@Composable
fun CenterAddressBanner(centerAddress: String?) {
    if (centerAddress.isNullOrBlank()) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 120.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = centerAddress,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
