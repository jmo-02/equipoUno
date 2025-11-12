package com.example.miniproyecto1.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.miniproyecto1.R
import com.example.miniproyecto1.data.InventoryDB
import java.text.NumberFormat
import java.util.Locale

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE = "com.example.miniproyecto1.widget.ACTION_TOGGLE_VISIBILITY"
        private const val PREFS = "inventory_widget_prefs"

        private fun prefs(context: Context) =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id -> updateWidget(context, manager, id) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val manager = AppWidgetManager.getInstance(context)
        when (intent.action) {
            ACTION_TOGGLE -> {
                val widgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                if (widgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    toggleVisibility(context, widgetId)
                    updateWidget(context, manager, widgetId)
                }
            }
        }
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        val visible = isVisible(context, widgetId)
        val total = calcularTotalInventario(context)

        if (visible) {
            views.setTextViewText(R.id.inv_balance, "$ ${formatMoney(total)}")
            views.setImageViewResource(R.id.inv_toggle_eye, R.drawable.ic_eye_closed)
        } else {
            views.setTextViewText(R.id.inv_balance, "$ ****")
            views.setImageViewResource(R.id.inv_toggle_eye, R.drawable.ic_eye_open)
        }

        // Toggle del ojo
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        val togglePI = PendingIntent.getBroadcast(
            context,
            widgetId,
            toggleIntent,
            pendingFlags()
        )
        views.setOnClickPendingIntent(R.id.inv_toggle_eye, togglePI)

        // Gestionar inventario → abre MainActivity (si quieres Login cambia la clase)
        val manageIntent = Intent(context, com.example.miniproyecto1.view.MainActivity::class.java)
        val managePI = PendingIntent.getActivity(
            context,
            widgetId + 1000,
            manageIntent,
            pendingFlags()
        )
        views.setOnClickPendingIntent(R.id.inv_manage_icon, managePI)
        views.setOnClickPendingIntent(R.id.inv_manage_text, managePI)

        manager.updateAppWidget(widgetId, views)
    }

    private fun calcularTotalInventario(context: Context): Double {
        return try {
            val dao = InventoryDB.getDatabase(context.applicationContext).inventoryDao()
            val items = dao.getAllItemsListSync()
            items.fold(0.0) { acc, item -> acc + (item.price.toDouble() * item.quantity) }
        } catch (e: Exception) {
            0.0
        }
    }

    private fun formatMoney(value: Double): String {
        val locale = Locale.forLanguageTag("es-CO")
        val nf = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            isGroupingUsed = true
        }
        return nf.format(value)
    }

    private fun isVisible(context: Context, widgetId: Int): Boolean =
        prefs(context).getBoolean("visible_$widgetId", false)

    private fun toggleVisibility(context: Context, widgetId: Int) {
        val current = isVisible(context, widgetId)
        prefs(context).edit().putBoolean("visible_$widgetId", !current).apply()
    }

    private fun pendingFlags(): Int {
        return PendingIntent.FLAG_UPDATE_CURRENT or
                (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    PendingIntent.FLAG_IMMUTABLE else 0)
    }
}