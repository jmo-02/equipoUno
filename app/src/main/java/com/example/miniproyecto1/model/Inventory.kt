package com.example.miniproyecto1.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "inventory",
    indices = [Index(value = ["code"], unique = true)]
)
data class Inventory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val code: Int ,
    val name: String ,
    val price: Int ,
    val quantity: Int
){
    // 3. Constructor sin argumentos explícito (Recomendado para Firebase)
    constructor() : this(
        id = 0,
        code = 0,
        name = "",
        price = 0,
        quantity = 0
    )
}