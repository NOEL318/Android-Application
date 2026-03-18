package com.example.application.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.AuthRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administrador") },
                actions = {
                    IconButton(onClick = {
                        authRepository.logout()
                        navController.navigate("login_screen") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text("Salir")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.navigate("admin_assign_role_screen") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Asignar Roles a Usuarios")
            }
            
            Button(
                onClick = { navController.navigate("admin_create_subject_screen") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Crear Materias")
            }

            Button(
                onClick = { navController.navigate("admin_assign_subject_screen") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Asignar Profesores a Materias")
            }

            Button(
                onClick = { navController.navigate("admin_assign_students_screen") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Asignar Alumnos a Materias")
            }
        }
    }
}
