package com.example.application.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

    LaunchedEffect(Unit) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Materias") },
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
                items(subjects) { subject ->
                    SubjectAssignItem(subject = subject, professors = professors, onAssign = { profId ->
                        scope.launch {
                            repository.assignSubjectToProfessor(subject.id, profId)
                            subjects = subjects.map { if (it.id == subject.id) it.copy(professorId = profId) else it }
                        }
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectAssignItem(subject: Subject, professors: List<User>, onAssign: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedProfessorName by remember { 
        mutableStateOf(professors.find { it.id == subject.professorId }?.name ?: "Sin asignar") 
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Materia: ${subject.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Descripción: ${subject.description}", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            
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
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
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
