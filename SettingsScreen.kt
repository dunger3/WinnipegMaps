package com.example.appdemo2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

///**
// * Displays app settings including theme toggle and login/logout controls.
// */
@Composable
fun SettingsScreen(
    darkTheme: Boolean,
    onToggleDarkTheme: () -> Unit,
    onLogout: () -> Unit,
    onShowLogin: () -> Unit,
    isLoggedIn: Boolean,
    isGuest: Boolean
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Settings", style = MaterialTheme.typography.titleLarge)

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dark Theme")
            Switch(
                checked = darkTheme,
                onCheckedChange = { onToggleDarkTheme() }
            )
        }

        Spacer(Modifier.height(20.dp))

        if (isGuest) {
            Button(
                onClick = onShowLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }

        if (isLoggedIn && !isGuest) {
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
