package com.example.appdemo2.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appdemo2.R
import com.example.appdemo2.data.AlertsRepository
import com.example.appdemo2.data.FavoriteRepository
import com.example.appdemo2.data.DirectionsApi
import com.example.appdemo2.data.GeoApi
import com.example.appdemo2.data.PlacesApi
import com.example.appdemo2.data.PlaceCategory
import com.example.appdemo2.data.PlaceMarker
import com.example.appdemo2.data.models.FavoriteLocation
import com.example.appdemo2.data.tempMarkers
import com.example.appdemo2.screens.CenterAddressBanner
import com.example.appdemo2.screens.RouteErrorBanner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

// Default downtown Winnipeg
private val DOWNTOWN_WINNIPEG = LatLng(49.8951, -97.1384)

///**
// * Main map screen that manages all map state, UI, dialogs, and user interactions.
// */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    darkTheme: Boolean,
    addMode: Boolean,
    onAddModeChange: (Boolean) -> Unit,
    showAddDialog: Boolean,
    onDismissDialog: () -> Unit,
    onConfirmAddFavorite: () -> Unit,
    isLoggedIn: Boolean,
    isGuest: Boolean,
    userId: String?
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val canEditFavorites = isLoggedIn && !isGuest
    val canCreateAlerts = isLoggedIn && !isGuest

    // --------------------------------
    // Firestore listeners
    // --------------------------------
    LaunchedEffect(userId) {
        if (canEditFavorites) FavoriteRepository.startListening()
    }
    LaunchedEffect(Unit) {
        AlertsRepository.startListening()
    }

    val favorites =
        if (canEditFavorites) FavoriteRepository.favorites else emptyList()
    val alerts = AlertsRepository.alerts

    // --------------------------------
    // Map / UI state
    // --------------------------------
    var selectedLocation by remember { mutableStateOf<FavoriteLocation?>(null) }
    var editedName by remember { mutableStateOf("") }

    var actionsMenuExpanded by remember { mutableStateOf(false) }
    var selectLocationMenuExpanded by remember { mutableStateOf(false) }

    var showDirectionDialog by remember { mutableStateOf(false) }
    var startFavorite by remember { mutableStateOf<FavoriteLocation?>(null) }
    var endFavorite by remember { mutableStateOf<FavoriteLocation?>(null) }
    var routePolyline by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var isRouteLoading by remember { mutableStateOf(false) }
    var routeError by remember { mutableStateOf<String?>(null) }

    // Alerts (community)
    var pendingAlertLocation by remember { mutableStateOf<LatLng?>(null) }
    var showAddAlertDialog by remember { mutableStateOf(false) }
    var alertTitle by remember { mutableStateOf("") }
    var alertDescription by remember { mutableStateOf("") }
    var alertPlaceMode by remember { mutableStateOf(false) }
    var pendingAlertDraft by remember { mutableStateOf<Pair<String, String>?>(null) }

    // Places state
    var gymMarkers by remember { mutableStateOf<List<PlaceMarker>>(emptyList()) }
    var gameMarkers by remember { mutableStateOf<List<PlaceMarker>>(emptyList()) }
    var pizzaMarkers by remember { mutableStateOf<List<PlaceMarker>>(emptyList()) }

    var showGyms by remember { mutableStateOf(false) }
    var showGames by remember { mutableStateOf(false) }
    var showPizza by remember { mutableStateOf(false) }

    // Geo / center info
    var centerAddress by remember { mutableStateOf<String?>(null) }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DOWNTOWN_WINNIPEG, 14f)
    }

    val mapProperties = MapProperties(
        isTrafficEnabled = true,
        mapStyleOptions =
            if (darkTheme)
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_style_night
                )
            else null
    )

    fun resetAlertDialogState() {
        alertTitle = ""
        alertDescription = ""
        pendingAlertLocation = null
        showAddAlertDialog = false
    }

    fun resetAlertPlacementState() {
        alertPlaceMode = false
        pendingAlertDraft = null
    }

    // --------------------------------
    // Root layout
    // --------------------------------
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        //Map content (markers, route, places)
        MapContent(
            mapProperties = mapProperties,
            cameraPositionState = cameraPositionState,
            canEditFavorites = canEditFavorites,
            favorites = favorites,
            alerts = alerts,
            routePolyline = routePolyline,
            gymMarkers = gymMarkers,
            gameMarkers = gameMarkers,
            pizzaMarkers = pizzaMarkers,
            showGyms = showGyms,
            showGames = showGames,
            showPizza = showPizza,
            onMapClick = { latLng ->
                when {
                    // Place alert at clicked location after drafting via "+"
                    canCreateAlerts && alertPlaceMode && pendingAlertDraft != null -> {
                        val (title, description) = pendingAlertDraft!!
                        scope.launch {
                            AlertsRepository.addAlert(
                                title = title.ifBlank { "Alert" },
                                description = description,
                                position = latLng
                            )
                        }
                        resetAlertDialogState()
                        resetAlertPlacementState()
                    }

                    // Add favorite in addMode
                    canEditFavorites && addMode -> {
                        scope.launch {
                            FavoriteRepository.addFavorite(
                                FavoriteLocation(
                                    name = "Favorite ${favorites.size}",
                                    position = latLng,
                                    description = "User-added"
                                )
                            )
                        }
                        onAddModeChange(false)
                    }

                    else -> {
                        // Just add a temp marker
                        tempMarkers.add(latLng)
                    }
                }
            },
            onMapLongClick = { latLng ->
                if (canCreateAlerts) {
                    // Long press → open dialog with fixed location
                    pendingAlertLocation = latLng
                    showAddAlertDialog = true
                }
            },
            onFavoriteMarkerClick = { fav ->
                selectedLocation = fav
                editedName = fav.name

                // Reverse geocode this favorite's position
                scope.launch {
                    centerAddress = "Loading address..."
                    val addr = GeoApi.reverseGeocode(fav.position)
                    centerAddress = addr ?: "Unknown location"
                }
            },
            onTempMarkerClick = { pos ->
                // Clicking temp marker also shows address
                scope.launch {
                    centerAddress = "Loading address..."
                    val addr = GeoApi.reverseGeocode(pos)
                    centerAddress = addr ?: "Unknown location"
                }
            }
        )

        //Dialogs (rename, add favorite, add alert, directions)
        MapDialogsSection(
            canEditFavorites = canEditFavorites,
            showAddDialog = showAddDialog,
            onDismissAddDialog = onDismissDialog,
            onConfirmAddFavorite = onConfirmAddFavorite,
            selectedLocation = selectedLocation,
            editedName = editedName,
            onEditedNameChange = { editedName = it },
            onRenameDismiss = { selectedLocation = null },
            onRenameSave = { loc, newName ->
                scope.launch {
                    FavoriteRepository.renameFavorite(loc.id, newName)
                }
                selectedLocation = null
            },
            canCreateAlerts = canCreateAlerts,
            showAddAlertDialog = showAddAlertDialog,
            pendingAlertLocation = pendingAlertLocation,
            alertTitle = alertTitle,
            alertDescription = alertDescription,
            onAlertTitleChange = { alertTitle = it },
            onAlertDescriptionChange = { alertDescription = it },
            onAlertDismiss = {
                resetAlertDialogState()
                resetAlertPlacementState()
            },
            onAlertSave = { loc, title, desc ->
                scope.launch {
                    AlertsRepository.addAlert(
                        title = title.ifBlank { "Alert" },
                        description = desc,
                        position = loc
                    )
                }
                resetAlertDialogState()
                resetAlertPlacementState()
            },
            favorites = favorites,
            showDirectionDialog = showDirectionDialog,
            startFavorite = startFavorite,
            endFavorite = endFavorite,
            onStartFavoriteChange = { startFavorite = it },
            onEndFavoriteChange = { endFavorite = it },
            onDirectionDialogDismiss = { showDirectionDialog = false },
            onDrawRoute = {
                if (startFavorite != null && endFavorite != null) {
                    scope.launch {
                        isRouteLoading = true
                        routeError = null
                        routePolyline = emptyList()

                        val points = DirectionsApi.getRoute(
                            startFavorite!!.position,
                            endFavorite!!.position
                        )

                        if (points.isEmpty()) {
                            routeError =
                                "No route found between selected favorites."
                        } else {
                            routePolyline = points
                            cameraPositionState.animate(
                                CameraUpdateFactory
                                    .newCameraPosition(
                                        CameraPosition(
                                            points.first(),
                                            12f,
                                            0f,
                                            0f
                                        )
                                    )
                            )
                        }

                        isRouteLoading = false
                    }
                }
            },
            onStartPlacingAlert = { title, desc ->
                // "+" Button → draft alert, then user taps on map to place it
                pendingAlertDraft = title to desc
                alertPlaceMode = true
                showAddAlertDialog = false
                alertTitle = ""
                alertDescription = ""
            },
            centerAddress = centerAddress
        )

        //Route error banner
        RouteErrorBanner(routeError)

        //Center address banner (GeoApi result)
        CenterAddressBanner(centerAddress)

        //Loading spinner while fetching route
        if (isRouteLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        //Controls (map options, legend, clear, + menu)
        MapControls(
            favorites = favorites,
            actionsMenuExpanded = actionsMenuExpanded,
            onActionsMenuExpandedChange = { actionsMenuExpanded = it },
            selectLocationMenuExpanded = selectLocationMenuExpanded,
            onSelectLocationMenuExpandedChange = { selectLocationMenuExpanded = it },
            onFavoriteSelectedFromMenu = { favorite ->
                scope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newCameraPosition(
                            CameraPosition(favorite.position, 14f, 0f, 0f)
                        )
                    )
                }
            },
            onFavoriteEditFromMenu = { favorite ->
                selectedLocation = favorite
                editedName = favorite.name
            },
            onFavoriteDeleteFromMenu = { favorite ->
                scope.launch {
                    FavoriteRepository.deleteFavorite(favorite.id)
                }
            },
            onShowDirectionsClick = { showDirectionDialog = true },
            onClearTempMarkersClick = { tempMarkers.clear() },
            onAddFavoriteClick = {
                onAddModeChange(false)
                onConfirmAddFavorite()
            },
            onAddAlertClick = {
                resetAlertPlacementState()
                pendingAlertLocation = null
                showAddAlertDialog = true
            },
            showGyms = showGyms,
            showGames = showGames,
            showPizza = showPizza,
            onToggleGyms = {
                showGyms = !showGyms
                if (showGyms && gymMarkers.isEmpty()) {
                    scope.launch {
                        gymMarkers = PlacesApi.searchNearby(
                            center = cameraPositionState.position.target,
                            radiusMeters = 3000.0,
                            includedType = "gym",
                            category = PlaceCategory.GYM
                        )
                    }
                }
            },
            onToggleGames = {
                showGames = !showGames
                if (showGames && gameMarkers.isEmpty()) {
                    scope.launch {
                        gameMarkers = PlacesApi.searchNearby(
                            center = cameraPositionState.position.target,
                            radiusMeters = 3000.0,
                            includedType = "electronics_store",
                            category = PlaceCategory.GAMES
                        )
                    }
                }
            },
            onTogglePizza = {
                showPizza = !showPizza
                if (showPizza && pizzaMarkers.isEmpty()) {
                    scope.launch {
                        pizzaMarkers = PlacesApi.searchNearby(
                            center = cameraPositionState.position.target,
                            radiusMeters = 3000.0,
                            includedType = "restaurant",
                            category = PlaceCategory.PIZZA
                        )
                    }
                }
            },
            onShowCenterInfoClick = {
                scope.launch {
                    centerAddress = "Loading address..."
                    val addr = GeoApi.reverseGeocode(
                        cameraPositionState.position.target
                    )
                    centerAddress = addr ?: "No address found for this location."
                }
            }
        )
    }
}
