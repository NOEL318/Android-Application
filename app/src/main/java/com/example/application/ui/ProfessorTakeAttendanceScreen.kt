package com.example.application.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
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
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessorTakeAttendanceScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { AcademicRepository() }
    val sessionManager = remember { SessionManager(context) }
    val professorId = sessionManager.getUserId() ?: ""
    val scope = rememberCoroutineScope()
    
    var subjects by remember { mutableStateOf<List<Subject>>(emptyList()) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var scannedContent by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        repository.getAllSubjects().onSuccess { allSub ->
            subjects = allSub.filter { it.professorId == professorId }
        }
    }

    // Configurar el launcher de ZXing para abrir la cámara nativa
    val qrLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, data)
            if (intentResult != null && intentResult.contents != null) {
                scannedContent = intentResult.contents
            } else {
                errorMessage = "Escaneo cancelado o fallido"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tomar Lista (Escanear QR)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("Atrás")
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
            if (subjects.isEmpty()) {
                Text("No tienes materias asignadas aún.")
            } else {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.let { "${it.name} - Sec: ${it.section}" } ?: "Selecciona tu materia actual",
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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        successMessage = null
                        errorMessage = null
                        val integrator = IntentIntegrator(context as Activity)
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                        integrator.setPrompt("Escanea el QR del alumno")
                        integrator.setBeepEnabled(true)
                        integrator.setOrientationLocked(false)
                        qrLauncher.launch(integrator.createScanIntent())
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedSubject != null
                ) {
                    Text("Abrir Cámara y Escanear QR")
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (scannedContent != null) {
                    val parsed = com.example.application.QrHelper.parseAttendanceQR(scannedContent!!)
                    if (parsed != null) {
                        val (qrStudentId, qrSubjectId) = parsed
                        
                        if (qrSubjectId == selectedSubject?.id) {
                            LaunchedEffect(scannedContent) {
                                val result = repository.recordAttendance(qrStudentId, qrSubjectId)
                                result.onSuccess {
                                    successMessage = "Asistencia registrada correctamente."
                                    scannedContent = null // Limpiar tras éxito
                                }.onFailure {
                                    errorMessage = "Error guardando en la BD."
                                    scannedContent = null
                                }
                            }
                        } else {
                            errorMessage = "El código QR pertenece a otra materia."
                            scannedContent = null
                        }
                    } else {
                        errorMessage = "Código QR inválido o expirado (5 mins límite)."
                        scannedContent = null
                    }
                }

                if (successMessage != null) {
                    Text(successMessage!!, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
                }
                if (errorMessage != null) {
                    Text(errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
