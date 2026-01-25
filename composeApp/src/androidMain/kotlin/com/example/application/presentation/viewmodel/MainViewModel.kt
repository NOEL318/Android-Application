package com.example.application.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    var isLoading by mutableStateOf(false)
        private set

    fun navegarConEspera(navController: NavController, route: String) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            delay(2000) // wait 2 sec
            isLoading = false
            navController.navigate(route)
        }
    }
    
    fun volverConEspera(navController: NavController) {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            delay(2000)
            isLoading = false
            navController.popBackStack()
        }
    }
}