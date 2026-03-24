package com.example.application

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.content.Context
import android.util.Log

class AuthRepository(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val sessionManager = SessionManager(context)

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("ID de usuario nulo")

            val documentSnapshot = firestore.collection("users").document(userId).get().await()

            if (documentSnapshot.exists()) {
                val user = documentSnapshot.toObject(User::class.java)
                if (user != null) {
                    sessionManager.createLoginSession(
                        userId = user.id,
                        email = user.email,
                        role = user.role
                    )
                    Result.success(user)
                } else {
                    Result.failure(Exception("No se pudieron obtener los datos del usuario"))
                }
            } else {
                Result.failure(Exception("El usuario no existe en la base de datos"))
            }

        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception("El correo electrónico no está registrado"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Contraseña incorrecta"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al iniciar sesión: ${e.message}")
            Result.failure(Exception("Error de conexión o datos inválidos"))
        }
    }

    suspend fun registerUser(name: String, email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("ID de usuario nulo")

            val newUser = User(
                id = userId,
                name = name,
                email = email,
                role = UserRole.ADMIN.name
            )

            firestore.collection("users").document(userId).set(newUser).await()

            sessionManager.createLoginSession(
                userId = newUser.id,
                email = newUser.email,
                role = newUser.role
            )
            Result.success(newUser)

        } catch (e: Exception) {
             Log.e("AuthRepository", "Error al registrar: ${e.message}")
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
        sessionManager.logoutUser()
    }
    
    fun getInitialRoute(): String {
        return if (sessionManager.isLoggedIn()) {
            when (sessionManager.getUserRole()) {
                UserRole.ADMIN.name -> "admin_dashboard"
                UserRole.PROFESSOR.name -> "professor_dashboard"
                UserRole.STUDENT.name -> "student_dashboard"
                else -> "login_screen"
            }
        } else {
            "login_screen"
        }
    }
}
