package com.example.application.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.AcademicRepository
import com.example.application.User
import com.example.application.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAssignRoleScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AcademicRepository() }
    val scope = rememberCoroutineScope()
    
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        repository.getAllUsers().onSuccess {
            users = it
            isLoading = false
        }.onFailure {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Roles") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                items(users) { user ->
                    UserRoleItem(user = user, onRoleChanged = { newRole ->
                        scope.launch {
                            repository.assignRole(user.id, newRole).onSuccess {
                                Toast.makeText(context, "Rol actualizado con éxito", Toast.LENGTH_SHORT).show()
                                // Actualizar la lista local
                                users = users.map { if (it.id == user.id) it.copy(role = newRole.name) else it }
                            }.onFailure {
                                Toast.makeText(context, "Error al actualizar rol", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun UserRoleItem(user: User, onRoleChanged: (UserRole) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Nombre: ${user.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Rol actual: ${user.role}", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { onRoleChanged(UserRole.STUDENT) }, enabled = user.role != UserRole.STUDENT.name) {
                    Text("Estudiante")
                }
                Button(onClick = { onRoleChanged(UserRole.PROFESSOR) }, enabled = user.role != UserRole.PROFESSOR.name) {
                    Text("Profesor")
                }
                Button(onClick = { onRoleChanged(UserRole.ADMIN) }, enabled = user.role != UserRole.ADMIN.name) {
                    Text("Admin")
                }
            }
        }
    }
}
