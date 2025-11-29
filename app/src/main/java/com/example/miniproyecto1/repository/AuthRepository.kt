package com.example.miniproyecto1.repository

import com.example.miniproyecto1.model.auth.UserRequest
import com.example.miniproyecto1.model.auth.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun registerUser(userRequest: UserRequest): UserResponse {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(
                userRequest.email,
                userRequest.password
            ).await()

            UserResponse(
                isSuccessful = true,
                message = "Registro exitoso. Bienvenido.",
                email = result.user?.email
            )
        } catch (e: Exception) {
            val message = when (e) {
                is FirebaseAuthUserCollisionException -> "Error en el registro: Este email ya está registrado."
                else -> "Error en el registro: Ocurrió un error inesperado."
            }
            UserResponse(
                isSuccessful = false,
                message = message,
                email = null
            )
        }
    }

    // Criterio 9 y 10: Maneja el proceso de inicio de sesión
    suspend fun loginUser(email: String, pass: String): UserResponse {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()

            // Login exitoso (Criterio 10)
            UserResponse(
                isSuccessful = true,
                message = "Login exitoso. Redirigiendo...",
                email = result.user?.email
            )
        } catch (e: Exception) {
            // Login incorrecto (Criterio 9)
            UserResponse(
                isSuccessful = false,
                message = "Login incorrecto", // Mensaje exacto requerido por Criterio 9
                email = null
            )
        }
    }
}