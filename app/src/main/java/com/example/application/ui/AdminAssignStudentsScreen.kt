package com.example.application.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun AdminAssignStudentsScreen(navController: NavController) {
    val repository = remember { AcademicRepository() }
    val scope = rememberCoroutineScope()
    
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var students by remember { mutableStateOf<List<User>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Alumnos") },
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
                    StudentAssignItem(subject = subject, students = students, onAssign = { studentId ->
                        scope.launch {
                            repository.enrollStudentToSubject(studentId, subject.id).onSuccess {
                                // Forzar recomposición actualizando el objeto materia en la lista
                                subjects = subjects.map {
                                    if (it.id == subject.id && !it.studentIds.contains(studentId)) {
                                        it.copy(studentIds = it.studentIds + studentId)
                                    } else it
                                }
                            }
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
fun StudentAssignItem(subject: Subject, students: List<User>, onAssign: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStudentName by remember { mutableStateOf("Seleccionar Alumno") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Materia: ${subject.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Sección: ${subject.section}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Alumnos inscritos: ${subject.studentIds.size}", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedStudentName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Agregar alumno") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    // Filtrar estudiantes que aún no están en la materia
                    val availableStudents = students.filter { !subject.studentIds.contains(it.id) }
                    
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
                                    // Resetear el placeholder visualmente para agregar otro luego
                                    selectedStudentName = "Seleccionar Alumno"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
