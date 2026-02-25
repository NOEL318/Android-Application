package com.example.application

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun App(userManager: UserManager) {
    MaterialTheme {
        var screenState by remember {
            mutableStateOf(if (userManager.isUserLoggedIn()) "INFO" else "LOGIN")
        }

        var formData by remember { mutableStateOf(mapOf<String, String>()) }
        var loginEmail by remember { mutableStateOf("") }
        var loginPass by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf<String?>(null) }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
            }

            when (screenState) {
                "REGISTER" -> {
                    Text("Registro Firebase", style = MaterialTheme.typography.headlineMedium)
                    val fields = listOf("Nombre", "Apellido", "Matricula", "Facultad", "Semestre", "Sexo", "correo", "password")

                    fields.forEach { field ->
                        OutlinedTextField(
                            value = formData[field] ?: "",
                            onValueChange = { newValue -> formData = formData + (field to newValue) },
                            label = { Text(field) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(onClick = {
                        userManager.registerUser(formData) { success, error ->
                            if (success) screenState = "INFO" else errorMessage = error
                        }
                    }, modifier = Modifier.padding(top = 10.dp)) {
                        Text("Registrar")
                    }

                    Button(onClick = { screenState = "LOGIN" }) { Text("Ya tengo cuenta") }
                }

                "LOGIN" -> {
                    Text("Login Firebase", style = MaterialTheme.typography.headlineMedium)
                    OutlinedTextField(value = loginEmail, onValueChange = { loginEmail = it }, label = { Text("Correo") })
                    OutlinedTextField(value = loginPass, onValueChange = { loginPass = it }, label = { Text("Password") })

                    Button(onClick = {
                        userManager.loginUser(loginEmail, loginPass) { success, error ->
                            if (success) screenState = "INFO" else errorMessage = error
                        }
                    }, modifier = Modifier.padding(top = 10.dp)) {
                        Text("Entrar")
                    }

                    Button(onClick = { screenState = "REGISTER" }) { Text("Crear cuenta") }
                }

                "INFO" -> {
                    val data = userManager.getData()
                    Text("Perfil de Usuario", style = MaterialTheme.typography.headlineMedium)
                    data.forEach { (key, value) -> Text("**$key**: $value") }

                    Button(onClick = {
                        userManager.logout()
                        screenState = "LOGIN"
                    }, modifier = Modifier.padding(top = 20.dp)) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}