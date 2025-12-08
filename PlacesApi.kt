package com.example.appdemo2.data

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

private const val PLACES_TAG = "PlacesApi"
private const val PLACES_URL = "https://places.googleapis.com/v1/places:searchNearby"

private val placesClient = OkHttpClient()

///**
// * Categories of supported place types for nearby search.
// */
enum class PlaceCategory {
    GYM,
    GAMES,
    PIZZA
}

///**
// * Represents a place result returned from a nearby Places API search.
// */
data class PlaceMarker(
    val name: String,
    val position: LatLng,
    val category: PlaceCategory
)

///**
// * Provides access to the Google Places API for searching nearby locations.
// */
object PlacesApi {

    ///**
    // * Searches for nearby places around a center point and returns matching results as place markers.
//     */
    suspend fun searchNearby(
        center: LatLng,
        radiusMeters: Double,
        includedType: String,
        category: PlaceCategory
    ): List<PlaceMarker> = withContext(Dispatchers.IO) {
        try {
            val apiKey = "AIzaSyC6smpCrPXQz8XiAAJ6Ew-RY8Eu_8sDIqo"
            if (apiKey.isBlank()) {
                Log.e(PLACES_TAG, "MAPS_API_KEY is missing")
                return@withContext emptyList()
            }

            val jsonBody = """
                {
                  "includedTypes": ["$includedType"],
                  "maxResultCount": 20,
                  "locationRestriction": {
                    "circle": {
                      "center": {
                        "latitude": ${center.latitude},
                        "longitude": ${center.longitude}
                      },
                      "radius": $radiusMeters
                    }
                  }
                }
            """.trimIndent()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(PLACES_URL)
                .addHeader("X-Goog-Api-Key", apiKey)
                .addHeader("X-Goog-FieldMask", "places.displayName,places.location")
                .post(body)
                .build()

            placesClient.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    Log.e(PLACES_TAG, "Places API call failed: HTTP ${response.code}")
                    return@withContext emptyList()
                }

                val json = JSONObject(responseBody)
                val placesArray = json.optJSONArray("places") ?: return@withContext emptyList()

                val result = mutableListOf<PlaceMarker>()
                for (i in 0 until placesArray.length()) {
                    val placeObj = placesArray.getJSONObject(i)

                    val displayNameObj = placeObj.optJSONObject("displayName")
                    val name = displayNameObj?.optString("text", "Unknown") ?: "Unknown"

                    val locationObj = placeObj.optJSONObject("location") ?: continue
                    val lat = locationObj.optDouble("latitude", Double.NaN)
                    val lng = locationObj.optDouble("longitude", Double.NaN)
                    if (lat.isNaN() || lng.isNaN()) continue

                    result.add(
                        PlaceMarker(
                            name = name,
                            position = LatLng(lat, lng),
                            category = category
                        )
                    )
                }

                result
            }
        } catch (e: Exception) {
            Log.e(PLACES_TAG, "Places API exception: ${e.message}", e)
            emptyList()
        }
    }
}
