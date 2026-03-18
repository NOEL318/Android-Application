package com.example.application.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.AcademicRepository
import com.example.application.SessionManager
import com.example.application.Subject
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEnrollScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AcademicRepository() }
    val sessionManager = remember { SessionManager(context) }
    val studentId = sessionManager.getUserId() ?: ""
    val scope = rememberCoroutineScope()

    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var enrolledSubjects by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val allSubjectsResult = repository.getAllSubjects()
        val studentSubjectsResult = repository.getEnrolledSubjects(studentId)

        allSubjectsResult.onSuccess { subjects = it }
        studentSubjectsResult.onSuccess { 
            enrolledSubjects = it.map { subject -> subject.id }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inscribir Materias") },
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
                    // Verificamos de forma reactiva si el ID está en la lista local para cambiar el botón
                    val isEnrolled = enrolledSubjects.contains(subject.id)
                    
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = subject.name, style = MaterialTheme.typography.titleMedium)
                                Text(text = "Sección: ${subject.section}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Button(
                                onClick = {
                                    scope.launch {
                                        repository.enrollStudentToSubject(studentId, subject.id).onSuccess {
                                            // Solo si es exitoso, forzamos a recomponer el UI agregando a la lista
                                            enrolledSubjects = enrolledSubjects + subject.id
                                        }
                                    }
                                },
                                enabled = !isEnrolled
                            ) {
                                Text(if (isEnrolled) "Inscrito" else "Inscribir")
                            }
                        }
                    }
                }
            }
        }
    }
}
