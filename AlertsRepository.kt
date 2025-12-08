package com.example.appdemo2.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.appdemo2.data.models.UserAlert
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

///**
// * Repository for managing real-time community user alerts.
// */
object AlertsRepository {

    private const val TAG = "AlertsRepository"
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null

    ///**
    // * A list of all community alerts synced from Firestore.
    // */
    val alerts: SnapshotStateList<UserAlert> = mutableStateListOf()

    ///**
    // * Starts listening for changes to the alerts collection in Firestore.
    // */
    fun startListening() {
        if (listenerRegistration != null) return

        listenerRegistration = db.collection("alerts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error listening for alerts", error)
                    return@addSnapshotListener
                }

                if (snapshot == null) return@addSnapshotListener

                alerts.clear()
                for (doc in snapshot.documents) {
                    val data = doc.data ?: continue
                    val title = data["title"] as? String ?: ""
                    val description = data["description"] as? String ?: ""
                    val lat = (data["lat"] as? Number)?.toDouble() ?: 0.0
                    val lng = (data["lng"] as? Number)?.toDouble() ?: 0.0

                    alerts.add(
                        UserAlert(
                            id = doc.id,
                            title = title,
                            description = description,
                            lat = lat,
                            lng = lng
                        )
                    )
                }
            }
    }

    ///**
    // * Adds a new user alert to Firestore.
    // */
    suspend fun addAlert(
        title: String,
        description: String,
        position: LatLng
    ) {
        val doc = mapOf(
            "title" to title,
            "description" to description,
            "lat" to position.latitude,
            "lng" to position.longitude
        )
        db.collection("alerts").add(doc).await()
    }

    ///**
    // * Deletes a specific alert from Firestore by its ID.
    // */
    suspend fun deleteAlert(id: String) {
        db.collection("alerts").document(id).delete().await()
    }
}
