package com.example.application.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.AcademicRepository
import com.example.application.Subject
import com.example.application.User
import com.example.application.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAssignSubjectScreen(navController: NavController) {
    val repository = remember { AcademicRepository() }
    val scope = rememberCoroutineScope()
    
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var professors by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    var subjectToDelete by remember { mutableStateOf<Subject?>(null) }

    fun refreshData() {
        isLoading = true
        scope.launch {
            val usersResult = repository.getAllUsers()
            val subjectsResult = repository.getAllSubjects()
            
            usersResult.onSuccess { allUsers ->
                professors = allUsers.filter { it.role == UserRole.PROFESSOR.name }
            }
            subjectsResult.onSuccess {
                subjects = it
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refreshData()
    }

    if (subjectToDelete != null) {
        AlertDialog(
            onDismissRequest = { subjectToDelete = null },
            title = { Text("Eliminar Materia") },
            text = { Text("¿Estás seguro de que deseas eliminar este registro?") },
            confirmButton = {
                TextButton(onClick = {
                    val id = subjectToDelete?.id ?: ""
                    scope.launch {
                        repository.deleteSubject(id).onSuccess {
                            refreshData()
                        }
                        subjectToDelete = null
                    }
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { subjectToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Materias") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                items(subjects) { subject ->
                    SubjectAssignItem(
                        subject = subject, 
                        professors = professors, 
                        onAssign = { profId ->
                            scope.launch {
                                repository.assignSubjectToProfessor(subject.id, profId)
                                subjects = subjects.map { if (it.id == subject.id) it.copy(professorId = profId) else it }
                            }
                        },
                        onDelete = {
                            subjectToDelete = subject
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectAssignItem(
    subject: Subject, 
    professors: List<User>, 
    onAssign: (String) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentProfessor = professors.find { it.id == subject.professorId }
    var selectedProfessorName by remember { 
        mutableStateOf(currentProfessor?.name ?: "Sin asignar") 
    }

    LaunchedEffect(subject.professorId, professors) {
        selectedProfessorName = professors.find { it.id == subject.professorId }?.name ?: "Sin asignar"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Materia: ${subject.name}", style = MaterialTheme.typography.titleMedium)
                    Text(text = "Sección: ${subject.section}", style = MaterialTheme.typography.bodyMedium)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar materia", tint = MaterialTheme.colorScheme.error)
                }
            }
            
            Text(text = "Descripción: ${subject.description}", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedProfessorName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Profesor Asignado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Sin asignar") },
                        onClick = {
                            selectedProfessorName = "Sin asignar"
                            expanded = false
                            onAssign("")
                        }
                    )
                    professors.forEach { prof ->
                        DropdownMenuItem(
                            text = { Text(text = prof.name) },
                            onClick = {
                                selectedProfessorName = prof.name
                                expanded = false
                                onAssign(prof.id)
                            }
                        )
                    }
                }
            }
        }
    }
}
