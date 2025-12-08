package com.example.appdemo2

import com.example.appdemo2.screens.LoginScreen
import com.example.appdemo2.screens.SettingsScreen
import com.example.appdemo2.screens.RegisterScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appdemo2.screens.*
import com.example.appdemo2.screens.map.MapScreen

//import com.example.appdemo2.screens.map.MapScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsDemoApp() {

    var darkTheme by rememberSaveable { mutableStateOf(false) }

    // User session state
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var isGuest by rememberSaveable { mutableStateOf(false) }
    var showRegister by rememberSaveable { mutableStateOf(false) }
    var userId by rememberSaveable { mutableStateOf<String?>(null) }

    // Navigation
    var currentScreen by rememberSaveable { mutableStateOf(MainScreen.Map) }

    // Map state
    var addMode by rememberSaveable { mutableStateOf(false) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    // -----------------------------------
    // LOGIN SCREEN
    // -----------------------------------
    if (!isLoggedIn && !showRegister) {
        LoginScreen(
            onLoginSuccess = { id ->
                userId = id
                isGuest = false
                isLoggedIn = true
                currentScreen = MainScreen.Map
            },
            onContinueAsGuest = {
                userId = null
                isGuest = true
                isLoggedIn = true        // required to enter the app
                currentScreen = MainScreen.Map
            },
            onCreateAccount = { showRegister = true }
        )
        return
    }

    // -----------------------------------
    // REGISTER SCREEN
    // -----------------------------------
    if (showRegister) {
        RegisterScreen(
            onRegisterSuccess = { id ->
                userId = id
                isGuest = false
                isLoggedIn = true
                showRegister = false
                currentScreen = MainScreen.Map
            },
            onCancel = {
                showRegister = false
            }
        )
        return
    }

    // -----------------------------------
    // MAIN APP UI
    // -----------------------------------
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (currentScreen) {
                            MainScreen.Map -> "Winnipeg Maps"
                            MainScreen.Favorites -> "Favorites"
                            MainScreen.Alerts -> "Alerts & Hazards"
                            MainScreen.Settings -> "Settings"
                        }
                    )
                },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        if (isGuest) {
                            Text("Guest")
                            Spacer(Modifier.width(10.dp))
                        }

                        Switch(
                            checked = darkTheme,
                            onCheckedChange = { darkTheme = !darkTheme }
                        )
                    }
                }
            )
        },

        bottomBar = {
            BottomNavigationBar(
                current = currentScreen,
                onScreenSelected = { currentScreen = it }
            )
        }
    ) { padding ->

        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (currentScreen) {

                MainScreen.Map ->
                    MapScreen(
                        darkTheme = darkTheme,
                        addMode = addMode,
                        onAddModeChange = { addMode = it },
                        showAddDialog = showAddDialog,
                        onDismissDialog = { showAddDialog = false },
                        onConfirmAddFavorite = {
                            showAddDialog = false
                            addMode = true
                        },
                        // 👇 pass auth state straight through
                        isLoggedIn = isLoggedIn,
                        isGuest = isGuest,
                        userId = if (isGuest) null else userId
                    )

                MainScreen.Favorites ->
                    FavoritesScreen(
                        userId = if (isGuest) null else userId
                    )

                MainScreen.Alerts ->
                    AlertsScreen(
                        // 👇 updated to match AlertsScreen signature
                        isLoggedIn = isLoggedIn,
                        isGuest = isGuest
                    )

                MainScreen.Settings ->
                    SettingsScreen(
                        darkTheme = darkTheme,
                        onToggleDarkTheme = { darkTheme = !darkTheme },
                        onLogout = {
                            userId = null
                            isLoggedIn = false
                            isGuest = false
                            showRegister = false
                            currentScreen = MainScreen.Map
                        },
                        onShowLogin = {
                            userId = null
                            isGuest = false
                            isLoggedIn = false
                            showRegister = false
                        },
                        isLoggedIn = !isGuest && isLoggedIn,
                        isGuest = isGuest
                    )
            }
        }
    }
}

enum class MainScreen {
    Map,
    Favorites,
    Alerts,
    Settings
}

@Composable
fun BottomNavigationBar(
    current: MainScreen,
    onScreenSelected: (MainScreen) -> Unit
) {
    NavigationBar {

        NavigationBarItem(
            selected = current == MainScreen.Map,
            onClick = { onScreenSelected(MainScreen.Map) },
            icon = { Icon(Icons.Default.Place, contentDescription = "Map") },
            label = { Text("Map") }
        )

        NavigationBarItem(
            selected = current == MainScreen.Favorites,
            onClick = { onScreenSelected(MainScreen.Favorites) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Favorites") },
            label = { Text("Favorites") }
        )

        NavigationBarItem(
            selected = current == MainScreen.Alerts,
            onClick = { onScreenSelected(MainScreen.Alerts) },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Alerts") },
            label = { Text("Alerts") }
        )

        NavigationBarItem(
            selected = current == MainScreen.Settings,
            onClick = { onScreenSelected(MainScreen.Settings) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text("Settings") }
        )
    }
}
