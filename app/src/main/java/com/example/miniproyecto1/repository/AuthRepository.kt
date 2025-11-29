package com.example.miniproyecto1.repository

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    fun isUserLogged(): Boolean = auth.currentUser != null

    fun getCurrentUser() = auth.currentUser

    fun logout() = auth.signOut()
}
