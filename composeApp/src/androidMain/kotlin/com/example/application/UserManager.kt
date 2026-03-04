package com.example.application

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class UserManager(private val context: Context) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

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
                    val uid = auth.currentUser?.uid ?: ""
                    // Guardar datos adicionales en Firestore
                    val userProfile = data.filterKeys { it != "password" }
                    db.collection("users").document(uid).set(userProfile)
                        .addOnSuccessListener {
                            saveCredentials(email, pass)
                            onResult(true, null)
                        }
                        .addOnFailureListener { e ->
                            onResult(false, e.message)
                        }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun loginUser(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveCredentials(email, pass)
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    private fun saveCredentials(email: String, pass: String) {
        prefs.edit().apply {
            putString("saved_email", email)
            putString("saved_password", pass)
            apply()
        }
    }

    fun getSavedCredentials(): Pair<String?, String?> {
        return Pair(prefs.getString("saved_email", null), prefs.getString("saved_password", null))
    }

    fun hasSavedCredentials(): Boolean {
        return prefs.contains("saved_email") && prefs.contains("saved_password")
    }

    fun getUserData(onResult: (Map<String, Any>?, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(null, "No user logged in")
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                onResult(document.data, null)
            }
            .addOnFailureListener { e ->
                onResult(null, e.message)
            }
    }

    fun logout() {
        auth.signOut()
        prefs.edit().remove("saved_email").remove("saved_password").apply()
    }

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showBiometricPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError("Autenticación biométrica fallida")
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticación biométrica")
            .setSubtitle("Inicie sesión usando su huella o rostro")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
