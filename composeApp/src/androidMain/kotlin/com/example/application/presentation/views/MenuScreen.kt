package com.example.application.presentation.views

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.application.domain.model.Platillo

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MenuScreen(
    tituloCategoria: String,
    nombreFiltro1: String,
    nombreFiltro2: String,
    listaPlatillos: List<Platillo>,
    onBack: () -> Unit
) {
    // ESTADOS PARA LOS FILTROS
    var filtro1Activo by remember { mutableStateOf(false) }
    var filtro2Activo by remember { mutableStateOf(false) }
    var filtroChefActivo by remember { mutableStateOf(false) }

    // LÓGICA DE FILTRADO REACTIVA
    // derivedStateOf asegura que la lista solo se recalcule si cambian los filtros
    val listaFiltrada by remember(filtro1Activo, filtro2Activo, filtroChefActivo) {
        derivedStateOf {
            listaPlatillos.filter { platillo ->
                // Lógica AND: Si el filtro está activo, el platillo debe cumplirlo.
                // Si no está activo, pasa automáticamente (true).
                val cumpleF1 = !filtro1Activo || platillo.esFiltro1
                val cumpleF2 = !filtro2Activo || platillo.esFiltro2
                val cumpleChef = !filtroChefActivo || platillo.esRecomendacionChef
                
                cumpleF1 && cumpleF2 && cumpleChef
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tituloCategoria) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // SECCIÓN DE FILTROS
            Text("Filtrar por:", style = MaterialTheme.typography.labelLarge)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                FilterChip(
                    selected = filtro1Activo,
                    onClick = { filtro1Activo = !filtro1Activo },
                    label = { Text(nombreFiltro1) }
                )
                FilterChip(
                    selected = filtro2Activo,
                    onClick = { filtro2Activo = !filtro2Activo },
                    label = { Text(nombreFiltro2) }
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = filtroChefActivo,
                    onClick = { filtroChefActivo = !filtroChefActivo },
                    label = { Text("👨‍🍳 Recomendación Chef") },
                    leadingIcon = { if (filtroChefActivo) Icon(Icons.Default.Star, null) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LISTA DE PLATILLOS (ListView en Compose es LazyColumn)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaFiltrada) { platillo ->
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlideImage(
                                    model = platillo.img_url,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .border(
                                            BorderStroke(1.dp, Color.Gray),
                                            AbsoluteRoundedCornerShape(10.dp)
                                        )
                                        .clip(AbsoluteRoundedCornerShape(10.dp))
                                        .width(110.dp)
                                )
                                Column(modifier = Modifier.padding(start = 8.dp)) {
                                    Text(
                                        text = platillo.nombre,
                                        fontSize = 20.sp,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(text = platillo.descripcion, style = MaterialTheme.typography.bodyMedium)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (platillo.esRecomendacionChef) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Default.Star, "Chef", tint = Color(0xFFDAA520))
                                        }
                                        Text(
                                            text = "$${platillo.precio}",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.primary)

                                    }
                                }
                            }


                        }
                    }
                }
            }
        }
    }
}