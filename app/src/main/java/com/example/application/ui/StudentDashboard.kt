package com.example.application.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.application.AcademicRepository
import com.example.application.AuthRepository
import com.example.application.QrHelper
import com.example.application.SessionManager
import com.example.application.Subject
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDashboard(navController: NavController) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }
    val sessionManager = remember { SessionManager(context) }
    val repository = remember { AcademicRepository() }
    
    val studentId = sessionManager.getUserId() ?: ""
    var enrolledSubjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var showQr by remember { mutableStateOf(false) }

    // Usar un key constante para que recargue cuando el estudiante inscribe una nueva materia
    LaunchedEffect(Unit) {
        repository.getEnrolledSubjects(studentId).onSuccess {
            enrolledSubjects = it
        }
    }
    
    val qrBitmap = remember(showQr, selectedSubject) {
        if (showQr && selectedSubject != null) {
            QrHelper.generateQRForAttendance(studentId, selectedSubject!!.id)
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Alumno") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("student_enroll_screen") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Inscribirse a Materias")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Pase de Lista", style = MaterialTheme.typography.titleMedium)
            
            if (enrolledSubjects.isEmpty()) {
                Text("Inscríbete a una materia primero.", style = MaterialTheme.typography.bodySmall)
            } else {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.name ?: "Selecciona una materia",
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
                        enrolledSubjects.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text("${subject.name} (Sec: ${subject.section})") },
                                onClick = {
                                    selectedSubject = subject
                                    expanded = false
                                    showQr = false // Ocultar si cambia materia
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showQr = !showQr },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedSubject != null
                ) {
                    Text(if (showQr) "Ocultar QR" else "Generar QR para Pase de Lista")
                }
                
                if (showQr && qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "Código QR de Asistencia",
                        modifier = Modifier.size(250.dp).padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { navController.navigate("student_grades_screen") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Mis Calificaciones")
            }
            
            Button(
                onClick = { navController.navigate("schedule_screen") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Mi Horario")
            }
        }
    }
}
