package com.example.application

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class UserManager(context: Context) {
    private val auth: FirebaseAuth = Firebase.auth
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // Registro en Firebase
    fun registerUser(data: Map<String, String>, onResult: (Boolean, String?) -> Unit) {
        val email = data["correo"] ?: ""
        val pass = data["password"] ?: ""

        if (email.isEmpty() || pass.isEmpty()) {
            onResult(false, "Correo o contraseña vacíos")
            return
        }

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveLocalData(data)
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    // Login en Firebase
    fun loginUser(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful, task.exception?.message)
            }
    }

    private fun saveLocalData(datos: Map<String, String>) {
        val editor = prefs.edit()
        datos.forEach { (key, value) -> editor.putString(key, value) }
        editor.apply()
    }

    fun getData(): Map<String, String?> {
        val fields = listOf("Nombre", "Apellido", "Matricula", "Facultad", "Semestre", "Sexo", "correo")
        return fields.associateWith { prefs.getString(it, "") }
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun logout() {
        auth.signOut()
        prefs.edit().clear().apply()
    }
}