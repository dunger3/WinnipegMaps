package com.example.appdemo2.screens.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.appdemo2.data.PlaceMarker
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

///**
// * Holds UI state for the map, including camera position, place markers, and reverse-geocoding results.
// */
class MapUiState {

    ///**
    // * The current camera position displayed on the map.
//     */
    var cameraPosition = CameraPosition.fromLatLngZoom(LatLng(49.8951, -97.1384), 14f)

    ///**
    // * Gym markers currently visible on the map.
//     */
    var gymMarkers by mutableStateOf<List<PlaceMarker>>(emptyList())

    ///**
    // * Video game store markers currently visible on the map.
//     */
    var gameMarkers by mutableStateOf<List<PlaceMarker>>(emptyList())

    ///**
    // * Pizza restaurant markers currently visible on the map.
//     */
    var pizzaMarkers by mutableStateOf<List<PlaceMarker>>(emptyList())

    ///**
    // * Whether gym markers should be shown.
//     */
    var showGyms by mutableStateOf(false)

    ///**
    // * Whether video game store markers should be shown.
//     */
    var showGames by mutableStateOf(false)

    ///**
    // * Whether pizza markers should be shown.
//     */
    var showPizza by mutableStateOf(false)

    ///**
    // * Indicates whether a reverse-geocoding lookup is currently loading.
//     */
    var reverseGeocodeLoading by mutableStateOf(false)

    ///**
    // * The resolved address returned from the most recent reverse-geocoding request.
//     */
    var reverseGeocodeResult by mutableStateOf<String?>(null)
}
