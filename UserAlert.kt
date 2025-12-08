package com.example.appdemo2.data.models

import com.google.android.gms.maps.model.LatLng

// Community alert: no userId, everyone sees them
data class UserAlert(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
) {
    val position: LatLng
        get() = LatLng(lat, lng)
}