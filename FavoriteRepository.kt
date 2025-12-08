package com.example.appdemo2.data

import androidx.compose.runtime.mutableStateListOf
import com.example.appdemo2.data.models.FavoriteLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing user-saved favorite map locations and address caching.
 */
object FavoriteRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * A live list of favorites automatically updated for UI screens.
     */
    val favorites = mutableStateListOf<FavoriteLocation>()

    private var listener: ListenerRegistration? = null

    /**
     * Starts listening for Firestore updates to the favorites collection.
     */
    fun startListening() {
        listener?.remove()

        listener = firestore.collection("favorites")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val updatedList = snapshot.documents.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val lat = doc.getDouble("lat") ?: return@mapNotNull null
                    val lng = doc.getDouble("lng") ?: return@mapNotNull null
                    val desc = doc.getString("description") ?: ""

                    FavoriteLocation(
                        id = doc.id,
                        name = name,
                        position = LatLng(lat, lng),
                        description = desc
                    )
                }

                favorites.clear()
                favorites.addAll(updatedList)
            }
    }

    /**
     * Adds a new favorite location to Firestore.
     */
    suspend fun addFavorite(location: FavoriteLocation) {
        firestore.collection("favorites")
            .add(
                mapOf(
                    "name" to location.name,
                    "lat" to location.position.latitude,
                    "lng" to location.position.longitude,
                    "description" to location.description
                )
            ).await()
    }

    /**
     * Renames an existing favorite by updating its Firestore document.
     */
    suspend fun renameFavorite(id: String, newName: String) {
        firestore.collection("favorites")
            .document(id)
            .update("name", newName)
            .await()
    }

    /**
     * Deletes a stored favorite location from Firestore by ID.
     */
    suspend fun deleteFavorite(id: String) {
        firestore.collection("favorites")
            .document(id)
            .delete()
            .await()
    }

    /**
     * Stores cached reverse-geocoded addresses so we don't repeatedly
     * call the Google Geocoding API for locations the user has already seen.
     */
    private object FavoriteAddressCache {
        private val cache = mutableMapOf<String, String?>()

        fun get(id: String): String? = cache[id]

        fun set(id: String, address: String?) {
            cache[id] = address
        }
    }

    /**
     * Returns a formatted human-readable address for a favorite location.
     *
     * - Returns cached value immediately (fast)
     * - If not cached, fetches it from GeoApi, stores it in the cache,
     *   and returns the result.
     *
     * @param fav The favorite location whose address is required.
     * @return The resolved address or `"Unknown location"` if lookup fails.
     */
    suspend fun getAddressForFavorite(fav: FavoriteLocation): String {
        //Check cache
        FavoriteAddressCache.get(fav.id)?.let { cached ->
            return cached
        }

        //Not cached → fetch from GeoApi
        val fetchedAddress = try {
            GeoApi.reverseGeocode(fav.position)
        } catch (_: Exception) {
            null
        }

        //Cache the result (even if null)
        FavoriteAddressCache.set(fav.id, fetchedAddress)

        return fetchedAddress ?: "Unknown location"
    }
}
