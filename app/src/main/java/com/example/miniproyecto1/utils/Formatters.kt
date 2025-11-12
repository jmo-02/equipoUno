package com.example.miniproyecto1.utils

import java.text.NumberFormat
import java.util.Locale

object Formatters {
    private val localeColombia = Locale("es","CO")
    private val nf = NumberFormat.getInstance(localeColombia).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }
    fun formatMoney(value: Double): String = nf.format(value)
}