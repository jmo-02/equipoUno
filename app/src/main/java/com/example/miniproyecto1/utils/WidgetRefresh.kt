package com.example.miniproyecto1.utils

import android.content.Context
import android.content.Intent
import com.example.miniproyecto1.widget.InventoryWidgetProvider

object WidgetRefresher {
    fun refresh(context: Context) {
        val intent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = InventoryWidgetProvider.ACTION_TOGGLE // O usa ACTION_REFRESH si luego la implementas
        }
        context.sendBroadcast(intent)
    }
}