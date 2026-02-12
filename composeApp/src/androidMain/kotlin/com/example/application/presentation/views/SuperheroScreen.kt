package com.example.application.presentation.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.application.domain.model.Superhero
import com.example.application.presentation.viewmodel.SuperheroViewModel
import androidx.compose.foundation.lazy.items

@Composable
fun SuperheroScreen(viewModel: SuperheroViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Buscador
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.searchQuery = it },
            label = { "Buscar Superhéroe" },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { viewModel.onSearch() }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.superheroes) { hero ->
                    SuperheroCard(hero)
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SuperheroCard(hero: Superhero) {
    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            GlideImage(
                model = hero.imageUrl,
                contentDescription = hero.name,
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(hero.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("Género: ${hero.gender}")
                Text("Raza: ${hero.race}")
                Text("Publisher: ${hero.publisher}")

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Los 3 poderes elegidos
                Text("🧠 Inteligencia: ${hero.intelligence}", style = MaterialTheme.typography.bodySmall)
                Text("💪 Fuerza: ${hero.strength}", style = MaterialTheme.typography.bodySmall)
                Text("⚡ Velocidad: ${hero.speed}", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}