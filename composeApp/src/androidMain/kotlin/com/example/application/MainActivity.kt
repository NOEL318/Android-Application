package com.example.application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.application.data.repository.AuthRepositoryImpl
import com.example.application.domain.model.Platillo
import com.example.application.domain.usecase.RegisterUserUseCase
import com.example.application.presentation.viewmodel.MainViewModel
import com.example.application.presentation.viewmodel.RegisterViewModel
import com.example.application.presentation.viewmodel.ViewModelFactory
import com.example.application.presentation.views.GalleryScreen
import com.example.application.presentation.views.HomeScreen
import com.example.application.presentation.views.InfoScreen
import com.example.application.presentation.views.MenuScreen
import com.example.application.presentation.views.RegisterScreen
import com.example.application.presentation.views.components.LoadingOverlay
import com.example.application.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // dependencies injection manual
        val authRepository = AuthRepositoryImpl()
        val registerUseCase = RegisterUserUseCase(authRepository)
        val regFactory = ViewModelFactory(registerUseCase)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // ViewModel Global to navigate and loads
                    val mainViewModel: MainViewModel = viewModel()

                    Box(modifier = Modifier.fillMaxSize()) {

                        NavHost(navController = navController, startDestination = "inicio") {

                            composable("inicio") {
                                HomeScreen(
                                    onNavigate = { ruta ->
                                        mainViewModel.navegarConEspera(navController, ruta)
                                    }
                                )
                            }

                            // --- CATEGORÍA 1: DESAYUNOS ---
                            composable("desayunos") {
                                // Datos simulados (Mock Data)
                                val datos = listOf(
                                    Platillo("Huevos Rancheros", "Salsa roja casera", 80.0, esRecomendacionChef = true),
                                    Platillo("Avena con Frutas", "Light y nutritiva", 60.0, esFiltro1 = true), // Saludable
                                    Platillo("Hot Cakes", "Miel de maple", 90.0, esFiltro2 = true, esRecomendacionChef = true) // Dulce
                                )
                                MenuScreen(
                                    tituloCategoria = "Desayunos",
                                    nombreFiltro1 = "🥗 Saludable",
                                    nombreFiltro2 = "🥞 Dulce",
                                    listaPlatillos = datos,
                                    onBack = { mainViewModel.volverConEspera(navController) }
                                )
                            }

                            // --- CATEGORÍA 2: PLATOS FUERTES ---
                            composable("platos_fuertes") {
                                val datos = listOf(
                                    Platillo("Enchiladas Suizas", "Pollo y queso manchego", 120.0, esRecomendacionChef = true),
                                    Platillo("Aguachile", "Camarón fresco y chile serrano", 150.0, esFiltro1 = true), // Picante
                                    Platillo("Hamburguesa Vegana", "Lentejas y avena", 110.0, esFiltro2 = true) // Vegano
                                )
                                MenuScreen(
                                    tituloCategoria = "Platos Fuertes",
                                    nombreFiltro1 = "🌶️ Picante",
                                    nombreFiltro2 = "🌱 Vegano",
                                    listaPlatillos = datos,
                                    onBack = { mainViewModel.volverConEspera(navController) }
                                )
                            }

                            // --- CATEGORÍA 3: POSTRES ---
                            composable("postres") {
                                val datos = listOf(
                                    Platillo("Pastel de Chocolate", "Doble fudge", 60.0, esFiltro1 = true, esRecomendacionChef = true),
                                    Platillo("Cheesecake Fresa", "Estilo New York", 70.0, esFiltro2 = true), // Frutal
                                    Platillo(
                                        "Brownie con Helado",
                                        "Nueces y vainilla",
                                        80.0,
                                        esFiltro1 = true
                                    ) // Chocolate
                                )
                                MenuScreen(
                                    tituloCategoria = "Postres",
                                    nombreFiltro1 = "🍫 Chocolate",
                                    nombreFiltro2 = "🍓 Frutal",
                                    listaPlatillos = datos,
                                    onBack = { mainViewModel.volverConEspera(navController) }
                                )
                            }

                            // --- CATEGORÍA 4: BEBIDAS ---
                            composable("bebidas") {
                                val datos = listOf(
                                    Platillo("Cerveza Artesanal", "IPA", 50.0, esFiltro1 = true), // Alcohol
                                    Platillo("Café Americano", "Grano selecto", 30.0, esFiltro2 = true), // Caliente
                                    Platillo("Carajillo", "Licor 43 shakeado", 110.0, esFiltro1 = true, esRecomendacionChef = true)
                                )
                                MenuScreen(
                                    tituloCategoria = "Bebidas",
                                    nombreFiltro1 = "🍺 Con Alcohol",
                                    nombreFiltro2 = "☕ Caliente",
                                    listaPlatillos = datos,
                                    onBack = { mainViewModel.volverConEspera(navController) }
                                )
                            }

                            composable("registro") {
                                // ViewModel de Registro (SÍ necesita factory)
                                val regViewModel: RegisterViewModel = viewModel(factory = regFactory)

                                RegisterScreen(
                                    viewModel = regViewModel,
                                    onBack = { mainViewModel.volverConEspera(navController) }
                                )
                            }

                            composable("info") {
                                InfoScreen(
                                    onBack = { mainViewModel.volverConEspera(navController) }
                                )
                            }

                            composable("galeria") {
                                GalleryScreen(
                                    onBack = { mainViewModel.volverConEspera(navController) }
                                )
                            }
                        }
                        LoadingOverlay(isVisible = mainViewModel.isLoading)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    AppTheme {
        HomeScreen(onNavigate = {})
    }
}