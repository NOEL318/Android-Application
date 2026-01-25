package com.example.application.presentation.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.application.domain.usecase.RegisterUserUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RegisterViewModel(private val registerUserUseCase: RegisterUserUseCase) : ViewModel() {

    // Ui state - context
    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var pais by mutableStateOf("México")
    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

    fun onRegister() {
        viewModelScope.launch {
            isLoading = true
            val result = registerUserUseCase(nombre, email, pais)
            isLoading = false

            result.onSuccess {
                message = "Registro Exitoso"
            }.onFailure {
                message = "Error: ${it.message}"
            }
        }
    }
}