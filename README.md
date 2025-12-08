Winnipeg Maps App

Developer: Dillon Unger
Status: In Development
Platform: Android (Jetpack Compose, Kotlin)

🗺️ Overview

Winnipeg Maps App is a custom-built mapping application using Google Maps Compose, Firestore, and Firebase-style authentication.
It provides a rich, interactive experience for both guests and authenticated users, offering features such as:

Saving personal favorite locations

Community alert posting

Viewing nearby places (gyms, pizza, video game stores)

Route drawing using Google Routes API

Reverse geocoding to display real-world addresses

The app is built with modular screens, clean architecture, and real-time Firestore syncing.

🧩 Features
👤 Guest vs Logged-In Mode

Guests can explore the map but cannot:

Save favorites

Delete/rename locations

Draw routes

Create community alerts

Logged-in users get full functionality, including:

Adding/editing/removing favorites

Creating alerts

Drawing routes

Viewing addresses for favorites & markers

⭐ Favorites System (Firestore-Synced)

Tap the map in "Add Mode" to save a new favorite.

Rename or delete favorites from the Map Options menu.

Each favorite resolves a reverse-geocoded address at runtime (not stored in Firestore).

Favorites automatically sync using Firestore listeners.

🚨 Community Alerts

Long-press the map to create an alert at that location.

Or use the Add Alert option to create a draft, then click anywhere to place it.

Alerts are visible to all users (community feature).

Firestore-based real-time sync.

🚗 Route Drawing (Google Routes API)

Select a start and end favorite using a clean popup dialog.

Fetches the optimal driving route using the modern Routes API v2.

Draws a polyline on the map.

Includes loading, error handling, and route clearing.

🏬 Places Search (Google Places API)

Toggle categories from a top-center legend:

Gyms

Video game stores

Pizza restaurants

Each toggle triggers a Nearby Search centered on the map’s camera position.
Markers are color-coded and do not require login.

📍 Reverse Geocoding (Google Geocoding API or Geolocate v2)

Clicking a favorite loads and displays its full address.

Temporary markers also display addresses (if enabled).

Addresses are cached in-memory using FavoriteRepository.FavoriteAddressCache.

🔧 Tech Stack

Kotlin + Jetpack Compose

Google Maps Compose

Google Places API

Google Routes API v2

Firestore Realtime Sync

OkHttp for API networking

MVVM-style repositories for clean separation

Material 3 UI

🔧 Google API Requirements

You must enable the following in your Google Cloud Console:

Required APIs:

Maps SDK for Android

Routes API

Geocoding API (or Geolocation API)

Places API (New)

Required Keys:

Stored in /local.properties:

MAPS_API_KEY=YOUR_API_KEY


And passed to the manifest:

manifestPlaceholders["MAPS_API_KEY"] = project.findProperty("MAPS_API_KEY")

🔥 Firestore Structure
Users
/users/{userId}
  firstName: String
  lastName: String
  username: String
  password: String

Favorites
/favorites/{favoriteId}
  userId: String
  name: String
  lat: Number
  lng: Number
  description: String

Alerts (community)
/alerts/{alertId}
  title: String
  description: String
  lat: Number
  lng: Number
  timestamp: Date

📦 Setup & Installation

Clone the project:

git clone https://github.com/dunger3/WinnipegMaps.git


Open in Android Studio.

Add your API keys to local.properties:

MAPS_API_KEY=YOUR_REAL_KEY


Enable required Google Cloud APIs.

Run on a device or emulator with Play Services.

📝 Status & Future Development

Planned features:

Offline caching

Improved clustering for places

Notification support for nearby community alerts

Firestore rules hardening

Saved routes history

👤 Credits

Developer: Dillon Unger
Instructor Guidance: Mobile App Development Course
