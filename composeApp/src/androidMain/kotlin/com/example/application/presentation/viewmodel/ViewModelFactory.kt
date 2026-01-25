package com.example.application.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.application.domain.usecase.RegisterUserUseCase

//para pasar parametros a viewmodel
class ViewModelFactory(private val registerUserUseCase: RegisterUserUseCase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(registerUserUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}