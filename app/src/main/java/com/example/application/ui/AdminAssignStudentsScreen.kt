package com.example.application.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.AcademicRepository
import com.example.application.Subject
import com.example.application.User
import com.example.application.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAssignStudentsScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AcademicRepository() }
    val scope = rememberCoroutineScope()
    
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var students by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var unenrollInfo by remember { mutableStateOf<Pair<User, Subject>?>(null) }

    fun refreshData() {
        isLoading = true
        scope.launch {
            val usersResult = repository.getAllUsers()
            val subjectsResult = repository.getAllSubjects()
            
            usersResult.onSuccess { allUsers ->
                students = allUsers.filter { it.role == UserRole.STUDENT.name }
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

    if (unenrollInfo != null) {
        val (student, subject) = unenrollInfo!!
        AlertDialog(
            onDismissRequest = { unenrollInfo = null },
            title = { Text("Desinscribir Alumno") },
            text = { Text("¿Estás seguro de que deseas eliminar este registro?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        repository.unenrollStudentFromSubject(student.id, subject.id).onSuccess {
                            Toast.makeText(context, "Alumno desinscrito con éxito", Toast.LENGTH_SHORT).show()
                            refreshData()
                        }.onFailure {
                            Toast.makeText(context, "Error al desinscribir alumno", Toast.LENGTH_SHORT).show()
                        }
                        unenrollInfo = null
                    }
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { unenrollInfo = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar Inscripciones") },
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
                    StudentAssignItem(
                        subject = subject, 
                        allStudents = students, 
                        onAssign = { studentId ->
                            scope.launch {
                                repository.enrollStudentToSubject(studentId, subject.id).onSuccess {
                                    Toast.makeText(context, "Alumno inscrito", Toast.LENGTH_SHORT).show()
                                    refreshData()
                                }.onFailure {
                                    Toast.makeText(context, "Error al inscribir alumno", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onUnenrollRequest = { student ->
                            unenrollInfo = Pair(student, subject)
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentAssignItem(
    subject: Subject, 
    allStudents: List<User>, 
    onAssign: (String) -> Unit,
    onUnenrollRequest: (User) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Materia: ${subject.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Sección: ${subject.section}", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Alumnos inscritos:", style = MaterialTheme.typography.labelLarge)
            
            val enrolledStudents = allStudents.filter { subject.studentIds.contains(it.id) }
            
            if (enrolledStudents.isEmpty()) {
                Text(text = "Ningún alumno inscrito", style = MaterialTheme.typography.bodySmall)
            } else {
                enrolledStudents.forEach { student ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = student.name, style = MaterialTheme.typography.bodyMedium)
                        IconButton(onClick = { onUnenrollRequest(student) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Desinscribir", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = "Inscribir nuevo alumno",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Agregar alumno") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    val availableStudents = allStudents.filter { !subject.studentIds.contains(it.id) }
                    
                    if (availableStudents.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Todos inscritos") },
                            onClick = { expanded = false }
                        )
                    } else {
                        availableStudents.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(text = st.name) },
                                onClick = {
                                    expanded = false
                                    onAssign(st.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
