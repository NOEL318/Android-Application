package com.example.application

import androidx.activity.compose.LocalActivity
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
import androidx.fragment.app.FragmentActivity

@Composable
fun App(userManager: UserManager) {
    val activity = LocalActivity.current as FragmentActivity
    
    var screenState by remember { 
        mutableStateOf(if (userManager.hasSavedCredentials()) "BIOMETRIC" else "LOGIN") 
    }
    
    var userData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Campos del perfil
    val profileFields = listOf("Nombre", "Apellido", "Matricula", "Facultad", "Semestre", "Sexo")
    var profileFormData by remember { mutableStateOf(profileFields.associateWith { "" }) }
    
    var loginEmail by remember { mutableStateOf("") }
    var loginPass by remember { mutableStateOf("") }

    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
            }

            when (screenState) {
                "BIOMETRIC" -> {
                    Text("Bienvenido de nuevo", style = MaterialTheme.typography.headlineMedium)
                    Button(onClick = {
                        userManager.showBiometricPrompt(activity, onSuccess = {
                            val (email, pass) = userManager.getSavedCredentials()
                            if (email != null && pass != null) {
                                userManager.loginUser(email, pass) { success, error ->
                                    if (success) {
                                        userManager.getUserData { data, err ->
                                            if (data != null) {
                                                userData = data
                                                screenState = "INFO"
                                            } else {
                                                errorMessage = err ?: "Error al cargar datos"
                                            }
                                        }
                                    } else {
                                        errorMessage = error
                                        screenState = "LOGIN"
                                    }
                                }
                            }
                        }, onError = { err ->
                            errorMessage = err
                        })
                    }) {
                        Text("Autenticar con Biometría")
                    }
                }

                "LOGIN" -> {
                    Text("Login Firebase", style = MaterialTheme.typography.headlineMedium)
                    OutlinedTextField(value = loginEmail, onValueChange = { loginEmail = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = loginPass, onValueChange = { loginPass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

                    Button(onClick = {
                        userManager.loginUser(loginEmail, loginPass) { success, error ->
                            if (success) {
                                userManager.getUserData { data, err ->
                                    if (data != null) {
                                        userData = data
                                        screenState = "INFO"
                                    } else {
                                        // Si no tiene datos de perfil, ir a registro de perfil? 
                                        // El requerimiento dice: una vez registrado pedir datos.
                                        errorMessage = "No se encontraron datos de perfil."
                                    }
                                }
                            } else {
                                errorMessage = error
                            }
                        }
                    }, modifier = Modifier.padding(top = 10.dp)) {
                        Text("Entrar")
                    }

                    Button(onClick = { screenState = "REGISTER" }) { Text("Crear cuenta") }
                }

                "REGISTER" -> {
                    Text("Registro", style = MaterialTheme.typography.headlineMedium)
                    OutlinedTextField(value = loginEmail, onValueChange = { loginEmail = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = loginPass, onValueChange = { loginPass = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

                    Button(onClick = {
                        // Primero registramos correo y pass
                        screenState = "PROFILE_SETUP"
                    }, modifier = Modifier.padding(top = 10.dp)) {
                        Text("Siguiente: Datos de Perfil")
                    }
                    Button(onClick = { screenState = "LOGIN" }) { Text("Ya tengo cuenta") }
                }

                "PROFILE_SETUP" -> {
                    Text("Datos de Perfil", style = MaterialTheme.typography.headlineMedium)
                    profileFields.forEach { field ->
                        OutlinedTextField(
                            value = profileFormData[field] ?: "",
                            onValueChange = { newValue -> profileFormData = profileFormData + (field to newValue) },
                            label = { Text(field) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(onClick = {
                        val fullData = profileFormData + ("correo" to loginEmail) + ("password" to loginPass)
                        userManager.registerUser(fullData) { success, error ->
                            if (success) {
                                userManager.getUserData { data, err ->
                                    userData = data
                                    screenState = "INFO"
                                }
                            } else {
                                errorMessage = error
                            }
                        }
                    }, modifier = Modifier.padding(top = 10.dp)) {
                        Text("Completar Registro")
                    }
                }

                "INFO" -> {
                    Text("Perfil de Usuario", style = MaterialTheme.typography.headlineMedium)
                    userData?.forEach { (key, value) ->
                        Text("$key: $value", modifier = Modifier.padding(vertical = 4.dp))
                    }

                    Button(onClick = {
                        userManager.logout()
                        userData = null
                        screenState = "LOGIN"
                    }, modifier = Modifier.padding(top = 20.dp)) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}
