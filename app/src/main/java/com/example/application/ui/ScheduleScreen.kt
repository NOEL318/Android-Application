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
import com.example.application.Schedule
import com.example.application.SessionManager
import com.example.application.Subject
import com.example.application.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AcademicRepository() }
    val sessionManager = remember { SessionManager(context) }
    
    val userId = sessionManager.getUserId() ?: ""
    val userRole = sessionManager.getUserRole() ?: UserRole.STUDENT.name

    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var subjectsMap by remember { mutableStateOf<Map<String, Subject>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val subjectsResult = repository.getAllSubjects()
        subjectsResult.onSuccess { allSubjects ->
            subjectsMap = allSubjects.associateBy { it.id }
        }

        if (userRole == UserRole.PROFESSOR.name) {
            repository.getProfessorSchedule(userId).onSuccess {
                schedules = it.sortedBy { s -> s.dayOfWeek }
            }
        } else {
            repository.getStudentSchedule(userId).onSuccess {
                schedules = it.sortedBy { s -> s.dayOfWeek }
            }
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Horario") },
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
        } else if (schedules.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tienes horarios asignados.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                items(schedules) { schedule ->
                    val subject = subjectsMap[schedule.subjectId]
                    ScheduleItem(schedule = schedule, subjectName = subject?.name ?: "Materia Desconocida", section = subject?.section ?: "")
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ScheduleItem(schedule: Schedule, subjectName: String, section: String) {
    val dayName = when(schedule.dayOfWeek) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        7 -> "Domingo"
        else -> "Día ${schedule.dayOfWeek}"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = dayName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(text = subjectName, style = MaterialTheme.typography.titleMedium)
            if (section.isNotEmpty()) {
                Text(text = "Sección: $section", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "🕒 ${schedule.startTime} - ${schedule.endTime}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "📍 Aula: ${schedule.classroom}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
