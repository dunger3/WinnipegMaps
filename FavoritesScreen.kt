package com.example.appdemo2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appdemo2.data.FavoriteRepository
import com.example.appdemo2.data.models.FavoriteLocation
import kotlinx.coroutines.launch

/**
 * Displays the logged-in user's saved favorites.
 *
 * Addresses are retrieved from FavoriteRepository, which handles
 * its own caching so the UI does not need to manage it.
 */
@Composable
fun FavoritesScreen(userId: String?) {

    if (userId == null) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "You must be logged in to view favorites.",
                style = MaterialTheme.typography.titleMedium
            )
        }
        return
    }

    val favorites = FavoriteRepository.favorites

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Your Favorite Places", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(favorites) { loc ->
                FavoriteListItem(loc)
            }
        }
    }
}

/**
 * One card entry for a favorite location.
 * Loads and displays an address using FavoriteRepository,
 * which handles both caching and API calls.
 */
@Composable
private fun FavoriteListItem(loc: FavoriteLocation) {
    val scope = rememberCoroutineScope()

    var address by remember { mutableStateOf("Loading address...") }
    var isLoading by remember { mutableStateOf(true) }

    // Load address once per location
    LaunchedEffect(loc.id) {
        scope.launch {
            val resolved = FavoriteRepository.getAddressForFavorite(loc)
            address = resolved
            isLoading = false
        }
    }

    ElevatedCard {
        Column(Modifier.padding(12.dp)) {

            Text(loc.name, style = MaterialTheme.typography.titleMedium)

            Text(
                if (isLoading) "Loading address..." else address,
                style = MaterialTheme.typography.bodyMedium
            )

            // Coordinates shown as fallback info
            Text(
                "Lat: ${loc.position.latitude}, Lng: ${loc.position.longitude}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
