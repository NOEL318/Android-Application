package com.example.application.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.example.application.presentation.viewmodel.RegisterViewModel

// inicio
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Desarrollo de Aplicaciones Móviles", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { onNavigate("registro") }) {
            Text("Ir a Registro")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = { onNavigate("galeria") }) { Text("Galería") }
        TextButton(onClick = { onNavigate("info") }) { Text("Info") }
    }
}

// registro
@Composable
fun RegisterScreen(viewModel: RegisterViewModel,
                   onBack: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val paises = listOf("México", "España", "Colombia", "Argentina")

    // Escuchar cambios en el mensaje para mostrar Toast o navegar (simplificado aquí)
    if (viewModel.message == "Registro Exitoso") {
        LaunchedEffect(Unit) {
            onBack()
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())
    ) {
        val (refTitulo, refForm, refBtn, refLoading) = createRefs()

        Text(
            "Registro",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.constrainAs(refTitulo) { top.linkTo(parent.top) }
        )

        Column(
            modifier = Modifier.constrainAs(refForm) {
                top.linkTo(refTitulo.bottom, margin = 32.dp)
            }
        ) {
            OutlinedTextField(
                value = viewModel.nombre,
                onValueChange = { viewModel.nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = viewModel.pais,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("País") },
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, "", Modifier.clickable { expanded = !expanded }) },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    paises.forEach { p ->
                        DropdownMenuItem(text = { Text(p) }, onClick = { viewModel.pais = p; expanded = false })
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.onRegister() },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth().constrainAs(refBtn) {
                top.linkTo(refForm.bottom, margin = 32.dp)
            }
        ) {
            Text(if (viewModel.isLoading) "Enviando..." else "Registrar")
        }
    }
}

// info
@Composable
fun InfoScreen(onBack: () -> Unit) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (logo, text, btn) = createRefs()
        Icon(
            Icons.Default.Info, "Logo",
            modifier = Modifier.size(80.dp).constrainAs(logo) {
                top.linkTo(parent.top, margin = 100.dp)
                start.linkTo(parent.start); end.linkTo(parent.end)
            }
        )
        Text(
            "App Para Algo aun no se que",
            modifier = Modifier.constrainAs(text) {
                top.linkTo(logo.bottom, margin = 16.dp)
                start.linkTo(parent.start); end.linkTo(parent.end)
            }
        )
        Button(
            onClick = { onBack() },
            modifier = Modifier.constrainAs(btn) {
                bottom.linkTo(parent.bottom, margin = 24.dp)
                end.linkTo(parent.end, margin = 24.dp)
            }
        ) { Text("Volver") }
    }
}

// galeria con grid layout
@Composable
fun GalleryScreen(onBack: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(6) { i ->
            Card(modifier = Modifier.padding(8.dp).height(150.dp)) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().background(Color.LightGray)) {
                    Text("Img $i")
                }
            }
        }
    }
}