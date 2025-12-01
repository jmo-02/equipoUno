package com.example.miniproyecto1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.miniproyecto1.model.Inventory
import com.example.miniproyecto1.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _listInventory = MutableLiveData<MutableList<Inventory>>()
    val listInventory: LiveData<MutableList<Inventory>> get() = _listInventory

    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> get() = _progressState

    // --- CRUD FIREBASE ---

    suspend fun saveInventory(inventory: Inventory): Boolean {
        _progressState.value = true
        return try {
            val result = repository.saveInventory(inventory)
            if (result) {
                val list = repository.getListInventory()
                _listInventory.postValue(list)
            }
            result
        } finally {
            _progressState.postValue(false)
        }
    }


    // ... en getListInventory()
    fun getListInventory() {
        viewModelScope.launch {
            _progressState.postValue(true) // Usar postValue
            try {
                val list = repository.getListInventory() // La llamada a la función es la que trae la lista
                _listInventory.postValue(list) // Usar postValue
            }catch (e: Exception){
                // Manejo de errores
            }
            finally {
                _progressState.postValue(false) // Usar postValue
            }
        }
    }

    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.deleteInventory(inventory)
                getListInventory()
            } finally {
                _progressState.value = false
            }
        }
    }

    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.updateInventory(inventory)
                getListInventory()
            } finally {
                _progressState.value = false
            }
        }
    }

    fun getById(id: Int, onResult: (Inventory?) -> Unit) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                onResult(repository.getById(id))
            } finally {
                _progressState.value = false
            }
        }
    }

    fun totalProducto(precio: Double, cantidad: Int): Double {
        return precio * cantidad
    }
}