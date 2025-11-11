package com.example.miniproyecto1.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.miniproyecto1.data.InventoryDB
import com.example.miniproyecto1.data.InventoryDao
import com.example.miniproyecto1.model.Inventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryRepository(context: Context) {

    private val inventoryDao: InventoryDao = InventoryDB.getDatabase(context).inventoryDao()

    /**
     * Guarda un producto en la base de datos.
     * Retorna true si se guarda correctamente, false si el código ya existe o hay error.
     */
    suspend fun saveInventory(inventory: Inventory): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                inventoryDao.insertItem(inventory)
                true
            } catch (e: SQLiteConstraintException) {
                // Error por duplicidad de 'code' (índice único)
                false
            } catch (e: Exception) {
                // Cualquier otro error general
                false
            }
        }
    }

    suspend fun getListInventory(): MutableList<Inventory> {
        return withContext(Dispatchers.IO) {
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
