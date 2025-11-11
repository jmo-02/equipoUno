package com.example.miniproyecto1.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val code: Int,
    val name: String,
    val price: Int,
    val quantity: Int
)



