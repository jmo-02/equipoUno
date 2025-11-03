package com.example.miniproyecto1.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.miniproyecto1.model.Inventory
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {

    // 1) Observador reactivo para la lista (LiveData) -> ideal para UI
    @Query("SELECT * FROM inventory ")
    fun getAllItemsLive(): LiveData<List<Inventory>>

    // 2) Flow si prefieres (alternativa moderna)
    @Query("SELECT * FROM inventory ")
    fun getAllItemsFlow(): Flow<List<Inventory>>

    // 3) Método suspend para uso puntual (ej. llamadas sincronas en repository)
    @Query("SELECT * FROM inventory")
    suspend fun getAllItemsList(): List<Inventory>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertItem(item: Inventory)

    @Update
    suspend fun updateItem(item: Inventory)

    @Delete
    suspend fun deleteItem(item: Inventory)

    @Query("SELECT * FROM inventory WHERE id = :id")
    suspend fun getItemById(id: Int): Inventory?
}

