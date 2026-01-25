package com.example.application.presentation.views

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.application.domain.model.User
import com.example.application.domain.repository.AuthRepository
import com.example.application.domain.usecase.RegisterUserUseCase
import com.example.application.presentation.viewmodel.RegisterViewModel

//Previews
@Preview(showBackground = true, name = "1. Inicio")
@Composable
fun PreviewHomeScreen() {
    // rememberNavController() crea un navegador falso que no hace nada per renderiza
    HomeScreen(navController = rememberNavController())
}

@Preview(showBackground = true, name = "3. Información")
@Composable
fun PreviewInfoScreen() {
    InfoScreen(navController = rememberNavController())
}

@Preview(showBackground = true, name = "4. Galería")
@Composable
fun PreviewGalleryScreen() {
    GalleryScreen(navController = rememberNavController())
}

// se simula el ViewModel y el repository
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "2. Registro")
@Composable
fun PreviewRegisterScreen() {
    // mockear repo
    val fakeRepo = object : AuthRepository { 
        override suspend fun registerUser(user: User): Boolean = true 
    }

//    create usecase and fake repo
    val fakeUseCase = RegisterUserUseCase(fakeRepo)
    val fakeViewModel = RegisterViewModel(fakeUseCase)

    // render
    RegisterScreen(navController = rememberNavController(), viewModel = fakeViewModel)
}