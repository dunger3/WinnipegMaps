package com.example.appdemo2.screens.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.appdemo2.data.PlaceMarker
import com.example.appdemo2.data.tempMarkers
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.example.appdemo2.data.models.FavoriteLocation
import com.example.appdemo2.data.models.UserAlert

///**
// * Displays the Google Map along with favorites, alerts, places, temporary markers, and optional route lines.
// */
@Composable
fun MapContent(
    mapProperties: MapProperties,
    cameraPositionState: CameraPositionState,
    canEditFavorites: Boolean,
    favorites: List<FavoriteLocation>,
    alerts: List<UserAlert>,
    routePolyline: List<LatLng>,
    gymMarkers: List<PlaceMarker>,
    gameMarkers: List<PlaceMarker>,
    pizzaMarkers: List<PlaceMarker>,
    showGyms: Boolean,
    showGames: Boolean,
    showPizza: Boolean,
    onMapClick: (LatLng) -> Unit,
    onMapLongClick: (LatLng) -> Unit,
    onFavoriteMarkerClick: (FavoriteLocation) -> Unit,
    onTempMarkerClick: (LatLng) -> Unit
) {
    GoogleMap(
        modifier = Modifier,
        properties = mapProperties,
        cameraPositionState = cameraPositionState,
        onMapClick = onMapClick,
        onMapLongClick = onMapLongClick
    ) {

        if (canEditFavorites) {
            favorites.forEach { favorite ->
                Marker(
                    state = MarkerState(favorite.position),
                    title = favorite.name,
                    snippet = favorite.description,
                    onClick = {
                        onFavoriteMarkerClick(favorite)
                        true
                    }
                )
            }
        }

        alerts.forEach { alert ->
            Marker(
                state = MarkerState(alert.position),
                title = alert.title,
                snippet = alert.description,
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_ORANGE
                )
            )
        }

        tempMarkers.forEach { pos ->
            Marker(
                state = MarkerState(pos),
                title = "Temp",
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE
                ),
                onClick = {
                    onTempMarkerClick(pos)
                    true
                }
            )
        }

        if (showGyms) {
            gymMarkers.forEach { place ->
                Marker(
                    state = MarkerState(place.position),
                    title = place.name,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_GREEN
                    )
                )
            }
        }

        if (showGames) {
            gameMarkers.forEach { place ->
                Marker(
                    state = MarkerState(place.position),
                    title = place.name,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_VIOLET
                    )
                )
            }
        }

        if (showPizza) {
            pizzaMarkers.forEach { place ->
                Marker(
                    state = MarkerState(place.position),
                    title = place.name,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_RED
                    )
                )
            }
        }

        if (routePolyline.isNotEmpty()) {
            Polyline(
                points = routePolyline,
                color = Color.Blue,
                width = 12f
            )
        }
    }
}
