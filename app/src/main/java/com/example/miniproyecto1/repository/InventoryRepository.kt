package com.example.miniproyecto1.repository

import android.content.Context
import com.example.miniproyecto1.model.Inventory
import com.example.miniproyecto1.data.InventoryDB
import com.example.miniproyecto1.data.InventoryDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryRepository(context: Context) {

    private val inventoryDao: InventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    suspend fun saveInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.insertItem(inventory)
        }
    }

    suspend fun getListInventory(): MutableList<Inventory> {
        return withContext(Dispatchers.IO) {
            // Puedes retornar una lista mutable desde la BD
            inventoryDao.getAllItemsList().toMutableList()
        }
    }

    suspend fun deleteInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.deleteItem(inventory)
        }
    }

    suspend fun updateInventory(inventory: Inventory) {
        withContext(Dispatchers.IO) {
            inventoryDao.updateItem(inventory)
        }
    }

    suspend fun getById(id: Int): Inventory? {
        return withContext(Dispatchers.IO) {
            inventoryDao.getItemById(id)
        }
    }
}
