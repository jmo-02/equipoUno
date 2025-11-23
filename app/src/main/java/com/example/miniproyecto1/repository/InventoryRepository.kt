package com.example.miniproyecto1.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import com.example.miniproyecto1.data.InventoryDB
import com.example.miniproyecto1.data.InventoryDao
import com.example.miniproyecto1.model.Inventory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InventoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val collection = firestore.collection("inventory")

    suspend fun saveInventory(inventory: Inventory): Boolean {
        return try {
            // Verificar si ya existe un producto con ese código
            val doc = collection.document(inventory.code.toString()).get().await()
            if (doc.exists()) return false

            // Guardar
            collection.document(inventory.code.toString()).set(inventory).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getListInventory(): MutableList<Inventory> {
        return try {
            val snapshot = collection.get().await()
            snapshot.toObjects(Inventory::class.java).toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }
    }

    suspend fun deleteInventory(inventory: Inventory) {
        try {
            collection.document(inventory.code.toString()).delete().await()
        } catch (e: Exception) { }
    }

    suspend fun updateInventory(inventory: Inventory) {
        try {
            collection.document(inventory.code.toString()).set(inventory).await()
        } catch (e: Exception) { }
    }

    suspend fun getById(id: Int): Inventory? {
        return try {
            val doc = collection.document(id.toString()).get().await()
            doc.toObject(Inventory::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
