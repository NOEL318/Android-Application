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
import com.example.application.Grade
import com.example.application.SessionManager
import com.example.application.Subject
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentGradesScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AcademicRepository() }
    val sessionManager = remember { SessionManager(context) }
    val studentId = sessionManager.getUserId() ?: ""

    var grades by remember { mutableStateOf<List<Grade>>(emptyList()) }
    var subjectsMap by remember { mutableStateOf<Map<String, Subject>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val gradesResult = repository.getStudentGrades(studentId)
        val subjectsResult = repository.getAllSubjects()

        gradesResult.onSuccess { studentGrades ->
            grades = studentGrades
        }
        subjectsResult.onSuccess { allSubjects ->
            subjectsMap = allSubjects.associateBy { it.id }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Calificaciones") },
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
        } else if (grades.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes calificaciones registradas aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                items(grades) { grade ->
                    val subject = subjectsMap[grade.subjectId]
                    GradeItem(grade = grade, subjectName = subject?.name ?: "Materia Desconocida", section = subject?.section ?: "")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun GradeItem(grade: Grade, subjectName: String, section: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = subjectName, style = MaterialTheme.typography.titleMedium)
            if (section.isNotEmpty()) {
                Text(text = "Sección: $section", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("P1: ${grade.partial1}")
                Text("P2: ${grade.partial2}")
                Text("P3: ${grade.partial3}")
                Text("P4: ${grade.partial4}")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(text = "Calificación Final: ${grade.finalScore}", style = MaterialTheme.typography.bodyLarge)
            if (grade.remarks.isNotBlank()) {
                Text(text = "Observaciones: ${grade.remarks}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
