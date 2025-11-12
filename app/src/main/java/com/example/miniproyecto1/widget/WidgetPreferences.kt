package com.example.miniproyecto1.widget

import android.content.Context
import android.content.SharedPreferences

object WidgetPreferences {
    private const val PREFS = "inventory_widget_prefs"
    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isVisible(context: Context, widgetId: Int): Boolean =
        prefs(context).getBoolean("visible_$widgetId", false)

    fun toggle(context: Context, widgetId: Int): Boolean {
        val current = isVisible(context, widgetId)
        prefs(context).edit().putBoolean("visible_$widgetId", !current).apply()
        return !current
    }
}