package com.example.application.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.AcademicRepository
import com.example.application.Grade
import com.example.application.SessionManager
import com.example.application.Subject
import com.example.application.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorGradesScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AcademicRepository() }
    val sessionManager = remember { SessionManager(context) }
    val professorId = sessionManager.getUserId() ?: ""
    val scope = rememberCoroutineScope()

    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var studentsMap by remember { mutableStateOf<Map<String, User>>(emptyMap()) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val allSubjectsResult = repository.getAllSubjects()
        val allUsersResult = repository.getAllUsers()

        allSubjectsResult.onSuccess { allSub ->
            // Filtra solo las materias que este profe imparte
            subjects = allSub.filter { it.professorId == professorId }
        }
        allUsersResult.onSuccess { allUsers ->
            studentsMap = allUsers.associateBy { it.id }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Calificaciones") },
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
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                if (subjects.isEmpty()) {
                    Text("No tienes materias asignadas aún.")
                } else {
                    // Selección de materia
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedSubject?.let { "${it.name} - Sec: ${it.section}" } ?: "Selecciona una materia",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Materia actual") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            subjects.forEach { subject ->
                                DropdownMenuItem(
                                    text = { Text("${subject.name} (Sec: ${subject.section})") },
                                    onClick = {
                                        selectedSubject = subject
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedSubject != null) {
                        // Idealmente, obtienes a los alumnos de selectedSubject.studentIds
                        // pero asumiendo que los alumnos se tienen que registrar, aquí podrías listar todos o solo los inscritos.
                        // Para este ejemplo, mostraremos todos los que sean STUDENT para poder calificar
                        val studentUsers = studentsMap.values.filter { it.role == com.example.application.UserRole.STUDENT.name }
                        
                        LazyColumn {
                            items(studentUsers) { student ->
                                AssignGradeItem(
                                    student = student,
                                    subject = selectedSubject!!,
                                    repository = repository
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssignGradeItem(student: User, subject: Subject, repository: AcademicRepository) {
    val scope = rememberCoroutineScope()
    
    var p1 by remember { mutableStateOf("") }
    var p2 by remember { mutableStateOf("") }
    var p3 by remember { mutableStateOf("") }
    var p4 by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Alumno: ${student.name}", style = MaterialTheme.typography.titleMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                OutlinedTextField(
                    value = p1, onValueChange = { p1 = it }, label = { Text("P1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = p2, onValueChange = { p2 = it }, label = { Text("P2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = p3, onValueChange = { p3 = it }, label = { Text("P3") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = p4, onValueChange = { p4 = it }, label = { Text("P4") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val np1 = p1.toDoubleOrNull() ?: 0.0
                val np2 = p2.toDoubleOrNull() ?: 0.0
                val np3 = p3.toDoubleOrNull() ?: 0.0
                val np4 = p4.toDoubleOrNull() ?: 0.0

                val newGrade = Grade(
                    studentId = student.id,
                    subjectId = subject.id,
                    partial1 = np1,
                    partial2 = np2,
                    partial3 = np3,
                    partial4 = np4
                )
                scope.launch {
                    repository.assignGrade(newGrade).onSuccess {
                        message = "Calificaciones guardadas"
                    }.onFailure {
                        message = "Error al guardar"
                    }
                }
            }) {
                Text("Guardar")
            }

            if (message != null) {
                Text(message!!, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
