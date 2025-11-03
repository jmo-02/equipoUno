package com.example.miniproyecto1.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto1.model.Inventory
import com.example.miniproyecto1.repository.InventoryRepository
import kotlinx.coroutines.launch

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = InventoryRepository(application.applicationContext)

    // Lista de inventario observable por la UI
    private val _listInventory = MutableLiveData<MutableList<Inventory>>()
    val listInventory: LiveData<MutableList<Inventory>> get() = _listInventory

    // Estado de progreso (para mostrar spinners o loading)
    private val _progressState = MutableLiveData(false)
    val progressState: LiveData<Boolean> get() = _progressState

    // --- CRUD ---

    fun saveInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.saveInventory(inventory)
                getListInventory() // recarga la lista después de guardar
            } finally {
                _progressState.value = false
            }
        }
    }

    fun getListInventory() {
        viewModelScope.launch {
            _progressState.value = true
            try {
                _listInventory.value = repository.getListInventory()
            } finally {
                _progressState.value = false
            }
        }
    }

    fun deleteInventory(inventory: Inventory) {
        viewModelScope.launch {
            _progressState.value = true
            try {
                repository.deleteInventory(inventory)
                getListInventory() // recarga lista tras eliminar
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
                getListInventory() // recarga lista tras actualizar
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
