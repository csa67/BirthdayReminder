package com.example.birthly

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    onSignIn: (String, String) -> Unit,
    onSignUp: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val interactionSource = remember { MutableInteractionSource() }

    fun validateInputs(): Boolean {
        var isValid = true

        if (email.isBlank()) {
            emailError = "Email cannot be empty"
            isValid = false
        } else {
            emailError = null
        }

        if (password.isBlank()) {
            passwordError = "Password cannot be empty"
            isValid = false
        } else {
            passwordError = null
        }

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = email,
            onValueChange = {
                email = it
                if (it.isNotBlank()) emailError = null
            },
            label = { Text("Enter Email") },
            isError = emailError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError != null) {
            Text(emailError!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        TextField(
            value = password,
            onValueChange = {
                password = it
                if (it.isNotBlank()) passwordError = null
            },
            label = { Text("Enter Password") },
            isError = passwordError != null,
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError != null) {
            Text(passwordError!!, color = androidx.compose.ui.graphics.Color.Red)
        }

        Button(
            onClick = {
                if (validateInputs()) {
                    onSignIn(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource
        ) {
            Text("Sign In")
        }

        Button(
            onClick = {
                if (validateInputs()) {
                    onSignUp(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            interactionSource = interactionSource
        ) {
            Text("Sign Up")
        }
    }
}
