package com.example.miniproyecto1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto1.model.auth.UserRequest
import com.example.miniproyecto1.model.auth.UserResponse
import com.example.miniproyecto1.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val AuthRepository: AuthRepository
) : ViewModel() {

    // LiveData para manejar el resultado del registro o el login
    private val _authResult = MutableLiveData<UserResponse>()
    val authResult: LiveData<UserResponse> = _authResult

    // LiveData para controlar la visibilidad de la barra de progreso
    private val _progressState = MutableLiveData<Boolean>()
    val progressState: LiveData<Boolean> = _progressState

    // LiveData para la validación en tiempo real de la contraseña
    private val _isPasswordValid = MutableLiveData<Boolean>()
    val isPasswordValid: LiveData<Boolean> = _isPasswordValid

    // LiveData para saber si los campos están listos para habilitar botones
    private val _areFieldsComplete = MutableLiveData<Boolean>()
    val areFieldsComplete: LiveData<Boolean> = _areFieldsComplete

    fun registerUser(userRequest: UserRequest) {
        // Muestra la barra de progreso (Criterio 15)
        _progressState.value = true
        viewModelScope.launch {
            // Criterio 13 y 14: Manejo del registro (éxito/error)
            val response = AuthRepository.registerUser(userRequest)
            _authResult.postValue(response)
            // Oculta la barra de progreso
            _progressState.postValue(false)
        }
    }

    fun loginUser(email: String, pass: String) {
        // Muestra la barra de progreso (Criterio 15)
        _progressState.value = true
        viewModelScope.launch {
            // Criterio 9 y 10: Manejo del login (éxito/error)
            val response = AuthRepository.loginUser(email, pass)
            _authResult.postValue(response)
            // Oculta la barra de progreso
            _progressState.postValue(false)
        }
    }

    fun validatePassword(password: String) {
        // Criterio 5: Tamaño mínimo de 6 números y máximo de 10 números
        val isValid = password.length in 6..10
        _isPasswordValid.value = isValid
    }

    fun checkFieldsCompletion(email: String, pass: String) {
        val isComplete = email.isNotEmpty() && pass.isNotEmpty()
        _areFieldsComplete.value = isComplete
    }
}