package com.example.miniproyecto1.model.auth

/**
 * Clase de datos que representa la información de login o registro
 * requerida por el LoginRepository.
 * * Contiene el email y la contraseña introducidos por el usuario.
 */
data class UserRequest(
    val email: String,
    val password: String
)