package com.example.appdemo2.data

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

///**
// * Provides reverse-geocoding utilities for converting coordinates into readable addresses.
// */
object GeoApi {

    private const val TAG = "GeoApi"
    private const val GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json"

    private val client = OkHttpClient()

    ///**
    // * Requests a human-readable address for the given coordinates using the Google Geocoding API.
//     */
    suspend fun reverseGeocode(latLng: LatLng): String? = withContext(Dispatchers.IO) {
        try {
            val apiKey = "Your API Key"
            if (apiKey.isBlank()) {
                Log.e(TAG, "MAPS_API_KEY is missing")
                return@withContext null
            }

            val url =
                "$GEOCODE_URL?latlng=${latLng.latitude},${latLng.longitude}&key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""

                if (!response.isSuccessful) return@withContext null

                val json = JSONObject(body)
                val results = json.optJSONArray("results") ?: return@withContext null
                if (results.length() == 0) return@withContext null

                val first = results.getJSONObject(0)
                first.optString("formatted_address", null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Reverse geocode failed: ${e.message}", e)
            null
        }
    }
}

