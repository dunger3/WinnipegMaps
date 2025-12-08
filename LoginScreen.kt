package com.example.appdemo2.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

///**
// * Displays the login screen with username, password, and guest/account creation options.
// */
@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onContinueAsGuest: () -> Unit,
    onCreateAccount: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                scope.launch {
                    db.collection("users")
                        .whereEqualTo("username", username)
                        .whereEqualTo("password", password)
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                onLoginSuccess(result.documents.first().id)
                            } else {
                                errorText = "Invalid login"
                            }
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onCreateAccount,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create New Account")
        }

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onContinueAsGuest,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue as Guest")
        }

        if (errorText.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(errorText, color = MaterialTheme.colorScheme.error)
        }
    }
}
