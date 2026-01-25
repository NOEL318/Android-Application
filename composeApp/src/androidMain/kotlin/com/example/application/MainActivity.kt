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
import com.example.application.domain.usecase.RegisterUserUseCase
import com.example.application.presentation.viewmodel.MainViewModel
import com.example.application.presentation.viewmodel.RegisterViewModel
import com.example.application.presentation.viewmodel.ViewModelFactory
import com.example.application.presentation.views.GalleryScreen
import com.example.application.presentation.views.HomeScreen
import com.example.application.presentation.views.InfoScreen
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