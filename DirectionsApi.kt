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

///**
// * Provides functions for requesting and decoding driving routes from the Google Directions API.
// */
object DirectionsApi {

    private const val TAG = "DirectionsApi"
    private const val ROUTES_URL =
        "https://routes.googleapis.com/directions/v2:computeRoutes"

    private val client = OkHttpClient()

    ///**
    // * Requests a driving route between two coordinates and returns the decoded polyline path.
//     */
    suspend fun getRoute(
        origin: LatLng,
        destination: LatLng
    ): List<LatLng> = withContext(Dispatchers.IO) {
        try {
            val apiKey = "AIzaSyC6smpCrPXQz8XiAAJ6Ew-RY8Eu_8sDIqo"
            if (apiKey.isBlank()) {
                Log.e(TAG, "MAPS_API_KEY is missing")
                return@withContext emptyList()
            }

            val jsonBody = """
                {
                  "origin": {
                    "location": {
                      "latLng": {
                        "latitude": ${origin.latitude},
                        "longitude": ${origin.longitude}
                      }
                    }
                  },
                  "destination": {
                    "location": {
                      "latLng": {
                        "latitude": ${destination.latitude},
                        "longitude": ${destination.longitude}
                      }
                    }
                  },
                  "travelMode": "DRIVE",
                  "routingPreference": "TRAFFIC_AWARE",
                  "computeAlternativeRoutes": false
                }
            """.trimIndent()

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toRequestBody(mediaType)

            val request = Request.Builder()
                .url(ROUTES_URL)
                .addHeader("X-Goog-Api-Key", apiKey)
                .addHeader("X-Goog-FieldMask", "routes.polyline.encodedPolyline")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    try {
                        val errJson = JSONObject(responseBody)
                        val errMsg = errJson
                            .optJSONObject("error")
                            ?.optString("message")
                        if (!errMsg.isNullOrBlank()) {
                            Log.e(TAG, "Routes API error: $errMsg")
                        }
                    } catch (_: Exception) { }
                    Log.e(TAG, "Routes API call failed: HTTP ${response.code}")
                    return@withContext emptyList()
                }

                val json = JSONObject(responseBody)
                val routesArray = json.optJSONArray("routes")
                if (routesArray == null || routesArray.length() == 0) {
                    return@withContext emptyList()
                }

                val firstRoute = routesArray.getJSONObject(0)
                val polylineObj = firstRoute.optJSONObject("polyline")
                val encodedPolyline = polylineObj?.optString("encodedPolyline", null)

                if (encodedPolyline.isNullOrEmpty()) {
                    return@withContext emptyList()
                }

                return@withContext decodePolyline(encodedPolyline)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Routes API exception: ${e.message}", e)
            emptyList()
        }
    }

    ///**
    // * Decodes an encoded Google polyline string into a list of map coordinates.
//     */
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val latD = lat / 1E5
            val lngD = lng / 1E5

            poly.add(LatLng(latD, lngD))
        }

        return poly
    }
}
