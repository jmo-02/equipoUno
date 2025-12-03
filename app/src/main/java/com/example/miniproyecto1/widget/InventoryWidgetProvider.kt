package com.example.miniproyecto1.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.example.miniproyecto1.R
import com.example.miniproyecto1.view.MainActivity
import com.google.firebase.auth.FirebaseAuth

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_TOGGLE = "com.example.miniproyecto1.widget.ACTION_TOGGLE_VISIBILITY"
        const val ACTION_MANAGE = "com.example.miniproyecto1.widget.ACTION_MANAGE_INVENTORY"
        private const val PREFS = "inventory_widget_prefs"

        private fun prefs(context: Context) =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, InventoryWidgetProvider::class.java)
            )
            if (ids.isNotEmpty()) {
                val intent = Intent(context, InventoryWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
            }
        }

        fun clearAllVisibilityPrefs(context: Context) {
            val p = prefs(context)
            p.edit().clear().apply()
            updateAllWidgets(context)
        }
    }

    override fun onUpdate(context: Context, manager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            val p = prefs(context)
            if (!p.contains("visible_$id")) {
                p.edit().putBoolean("visible_$id", false).apply()
            }
            updateWidget(context, manager, id)
        }
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
                    handleEyeClick(context, manager, widgetId)
                }
            }
            ACTION_MANAGE -> {
                val widgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                handleManageClick(context, widgetId)
            }
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                if (ids != null) onUpdate(context, AppWidgetManager.getInstance(context), ids)
            }
        }
    }

    private fun handleEyeClick(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            toggleVisibility(context, widgetId)
            updateWidget(context, manager, widgetId)
        } else {
            val loginIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("fromWidget", true)
                putExtra("widgetAction", "toggleBalance")
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            context.startActivity(loginIntent)
        }
    }

    private fun handleManageClick(context: Context, widgetId: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("fromWidget", true)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            putExtra("widgetAction", if (currentUser != null) "goToHome" else "manageInventory")
        }
        context.startActivity(intent)
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            views.setTextViewText(R.id.inv_balance, "$ ****")
            views.setImageViewResource(R.id.inv_toggle_eye, R.drawable.ic_eye_open)
        } else {
            val visible = isVisible(context, widgetId)
            val cachedTotal = readCachedTotal(context) // lee el total formateado desde prefs
            if (visible) {
                views.setTextViewText(R.id.inv_balance, "$ $cachedTotal")
                views.setImageViewResource(R.id.inv_toggle_eye, R.drawable.ic_eye_closed)
            } else {
                views.setTextViewText(R.id.inv_balance, "$ ****")
                views.setImageViewResource(R.id.inv_toggle_eye, R.drawable.ic_eye_open)
            }
        }

        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        val togglePI = PendingIntent.getBroadcast(context, widgetId, toggleIntent, pendingFlags())
        views.setOnClickPendingIntent(R.id.inv_toggle_eye, togglePI)

        val manageIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_MANAGE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        val managePI = PendingIntent.getBroadcast(context, widgetId + 1000, manageIntent, pendingFlags())
        views.setOnClickPendingIntent(R.id.inv_manage_icon, managePI)
        views.setOnClickPendingIntent(R.id.inv_manage_text, managePI)

        manager.updateAppWidget(widgetId, views)
    }

    private fun readCachedTotal(context: Context): String {
        val prefs = context.getSharedPreferences("inventory_widget_cache", Context.MODE_PRIVATE)
        // Si no hay cache, muestra 0,00 con el formato correcto para evitar 0 “feo”
        return prefs.getString("total_formatted", "0,00") ?: "0,00"
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
