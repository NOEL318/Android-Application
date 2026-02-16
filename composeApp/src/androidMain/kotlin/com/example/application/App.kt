package com.example.application

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

import application.composeapp.generated.resources.Res
import application.composeapp.generated.resources.compose_multiplatform

@Composable
fun App(userManager: UserManager) {
    MaterialTheme {
        var screenState by remember {
            mutableStateOf(if (userManager.isRegistered()) "LOGIN" else "REGISTER")
        }

        // Form states
        var formData by remember { mutableStateOf(mutableMapOf<String, String>()) }
        var loginEmail by remember { mutableStateOf("") }
        var loginPass by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (screenState) {
                "REGISTER" -> {
                    Text("Registro de Usuario", style = MaterialTheme.typography.headlineMedium)
                    val fields = listOf("Nombre", "Apellido", "Matricula", "Facultad", "Semestre", "Sexo", "correo", "password")

                    fields.forEach { field ->
                        OutlinedTextField(
                            value = formData[field] ?: "",
                            onValueChange = { formData = formData.toMutableMap().apply { put(field, it) } },
                            label = { Text(field) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(onClick = {
                        userManager.saveData(formData)
                        screenState = "INFO"
                    }, modifier = Modifier.padding(top = 10.dp)) {
                        Text("Guardar y Registrar")
                    }
                }

                "LOGIN" -> {
                    val savedData = userManager.getData()
                    Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)
                    OutlinedTextField(value = loginEmail, onValueChange = { loginEmail = it }, label = { Text("Correo") })
                    OutlinedTextField(value = loginPass, onValueChange = { loginPass = it }, label = { Text("Contraseña") })

                    Button(onClick = {
                        if (loginEmail == savedData["correo"] && loginPass == savedData["password"]) {
                            screenState = "INFO"
                        }
                    }, modifier = Modifier.padding(top = 10.dp)) {
                        Text("Entrar")
                    }
                }

                "INFO" -> {
                    val data = userManager.getData()
                    Text("Información del Perfil", style = MaterialTheme.typography.headlineMedium)
                    data.forEach { (key, value) ->
                        if (key != "password") Text("**$key**: $value")
                    }

                    Button(onClick = {
                        userManager.clearData()
                        screenState = "REGISTER"
                    }, modifier = Modifier.padding(top = 20.dp)) {
                        Text("Cerrar Sesión y Borrar Datos")
                    }
                }
            }
        }
    }
}