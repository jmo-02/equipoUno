package com.example.miniproyecto1.model.auth

data class UserResponse(
    val isSuccessful: Boolean, // true si el login/registro fue exitoso (Criterios 10, 14)
    val message: String,      // Mensaje a mostrar en el Toast (Criterios 9, 13)
    val email: String? = null // Email del usuario si el login/registro fue exitoso
)