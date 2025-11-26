package com.example.miniproyecto1.viewmodel

import androidx.lifecycle.ViewModel
import com.example.miniproyecto1.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun isUserLogged() = authRepository.isUserLogged()

    fun getCurrentUser() = authRepository.getCurrentUser()

    fun logout() = authRepository.logout()
}
