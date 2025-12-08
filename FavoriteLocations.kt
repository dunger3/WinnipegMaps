package com.example.appdemo2.data.models

import com.google.android.gms.maps.model.LatLng

data class FavoriteLocation(
    val id: String = "",
    val name: String,
    val position: LatLng,
    val description: String
)
