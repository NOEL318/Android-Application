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
                                    Platillo("Huevos Rancheros",
                                        "Salsa roja casera",
                                        80.0,
                                        esRecomendacionChef = true,
                                        img_url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRRi30va67R11hQqpQYBT9p7OyIzETr_iQpcBZypJlqjFWOJ6z5fV_sNvVKw89WaMF-ehK_rQBExZ250bDYUe-gNeQLQ7xiG5xtBDLEhS8&s=10"),
                                    Platillo("Avena con Frutas",
                                        "Light y nutritiva",
                                        60.0,
                                        esFiltro1 = true,
                                        img_url = "https://granvita.com/wp-content/uploads/2024/03/bowl-de-avena-con-fruta.jpg"), // Saludable
                                    Platillo("Hot Cakes",
                                        "Miel de maple",
                                        90.0,
                                        esFiltro2 = true,
                                        esRecomendacionChef = true,
                                        img_url = "https://images.aws.nestle.recipes/original/4ba3978c241c628affcaf5c4e837270e_hot_cakes_clasicos_-_desayuno.jpg") // Dulce
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
                                    Platillo("Enchiladas Suizas", "Pollo y queso manchego",
                                        120.0,
                                        esRecomendacionChef = true,
                                        img_url = "https://recetinas.com/wp-content/uploads/2018/05/enchiladas-suizas.jpg"),
                                    Platillo("Aguachile",
                                        "Camarón fresco y chile serrano",
                                        150.0,
                                        esFiltro1 = true,
                                        img_url = "https://editorialtelevisa.brightspotcdn.com/dims4/default/b0d270a/2147483647/strip/true/crop/700x700+250+0/resize/1000x1000!/quality/90/?url=https%3A%2F%2Fk2-prod-editorial-televisa.s3.us-east-1.amazonaws.com%2Fbrightspot%2F6e%2F64%2Fecd269fa448f84facae9cd8254ce%2Faguachile-sinaloa.jpg"), // Picante
                                    Platillo("Hamburguesa Vegana",
                                        "Lentejas y avena",
                                        110.0,
                                        esFiltro2 = true,
                                        img_url = "https://www.conasi.eu/blog/wp-content/uploads/2022/05/hamburguesa-vegana-1.jpg") // Vegano
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
                                    Platillo("Pastel de Chocolate",
                                        "Doble fudge",
                                        60.0,
                                        esFiltro1 = true,
                                        esRecomendacionChef = true,
                                        img_url = "https://consola.lazarza.com.mx//storage/176/choco.png"),
                                    Platillo("Cheesecake Fresa",
                                        "Estilo New York",
                                        70.0, esFiltro2 = true,
                                        img_url = "https://www.splenda.com/wp-content/themes/bistrotheme/assets/recipe-images/strawberry-topped-cheesecake.jpg"), // Frutal
                                    Platillo(
                                        "Brownie con Helado",
                                        "Nueces y vainilla",
                                        80.0,
                                        esFiltro1 = true,
                                        img_url = "https://chefeel.com/chefgeneralfiles/2023/08/rebanada-brownie-chocolate-helado-nuez-vainilla-880x875.jpg"
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
                                    Platillo("Cerveza Artesanal",
                                        "IPA",
                                        50.0,
                                        esFiltro1 = true,
                                        img_url = "https://i0.wp.com/www.quepasaoaxaca.com/wp-content/uploads/2017/11/beer.jpg?fit=960%2C600&ssl=1"), // Alcohol
                                    Platillo("Café Americano",
                                        "Grano selecto",
                                        30.0,
                                        esFiltro2 = true,
                                        img_url = "https://excelso77.com/wp-content/uploads/2024/05/por-que-el-cafe-americano-se-llama-asi-te-lo-contamos.webp"), // Caliente
                                    Platillo("Carajillo",
                                        "Licor 43 shakeado",
                                        110.0,
                                        esFiltro1 = true,
                                        esRecomendacionChef = true,
                                        img_url = "https://i.blogs.es/26d9c0/1366_2000-44-/450_1000.jpg")
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