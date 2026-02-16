package com.example.application// En commonMain o androidMain (para este ejemplo usaremos el contexto de Android)
import android.content.Context
import android.content.SharedPreferences

class UserManager(context: Context) {
    private val prefs: SharedPreferences = 
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveData(datos: Map<String, String>) {
        val editor = prefs.edit()
        datos.forEach { (key, value) -> editor.putString(key, value) }
        editor.putBoolean("is_registered", true)
        editor.apply()
    }

    fun getData(): Map<String, String> {
        return mapOf(
            "Nombre" to (prefs.getString("Nombre", "") ?: ""),
            "Apellido" to (prefs.getString("Apellido", "") ?: ""),
            "Matricula" to (prefs.getString("Matricula", "") ?: ""),
            "Facultad" to (prefs.getString("Facultad", "") ?: ""),
            "Semestre" to (prefs.getString("Semestre", "") ?: ""),
            "Sexo" to (prefs.getString("Sexo", "") ?: ""),
            "correo" to (prefs.getString("correo", "") ?: ""),
            "password" to (prefs.getString("password", "") ?: "")
        )
    }

    fun isRegistered(): Boolean = prefs.getBoolean("is_registered", false)

    fun clearData() {
        prefs.edit().clear().apply()
    }
}