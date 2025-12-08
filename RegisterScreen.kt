package com.example.appdemo2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore

///**
// * Displays the user registration form and creates a new account in Firestore.
// */
@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onCancel: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Create Account", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                if (firstName.isBlank() || lastName.isBlank() ||
                    username.isBlank() || password.isBlank()
                ) {
                    errorMessage = "Please fill out all fields."
                    return@Button
                }

                val userData = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "username" to username,
                    "password" to password
                )

                db.collection("users")
                    .add(userData)
                    .addOnSuccessListener { doc ->
                        onRegisterSuccess(doc.id)
                    }
                    .addOnFailureListener {
                        errorMessage = "Error creating account."
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to login")
        }
    }
}
