package com.example.miniproyecto1.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.miniproyecto1.R
import com.example.miniproyecto1.widget.InventoryWidgetProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navigationContainer) as NavHostFragment
        navController = navHostFragment.navController

        handleWidgetNavigation()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)          // Actualiza los extras disponibles en getIntent()
        handleWidgetNavigation()   // Re-evalúa navegación según widgetAction
    }

    private fun handleWidgetNavigation() {
        val fromWidget = intent.getBooleanExtra("fromWidget", false)
        val widgetAction = intent.getStringExtra("widgetAction")

        if (fromWidget && widgetAction != null) {
            val currentUser = FirebaseAuth.getInstance().currentUser
            when (widgetAction) {
                "toggleBalance" -> {
                    if (currentUser != null) {
                        InventoryWidgetProvider.updateAllWidgets(this)
                        finish() // Volver al widget
                    }
                    // Si no está logueado, nav_graph abre LoginFragment
                }
                "manageInventory" -> {
                    // No logueado: nav_graph abrirá Login; LoginFragment después navegará al Home.
                }
                "goToHome" -> {
                    if (currentUser != null) {
                        navController.navigate(R.id.homeInventoryFragment)
                    }
                }
            }
        }
    }

    fun getWidgetAction(): String? = intent.getStringExtra("widgetAction")
    fun isFromWidget(): Boolean = intent.getBooleanExtra("fromWidget", false)
    fun getWidgetId(): Int = intent.getIntExtra(
        android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID,
        android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
    )
}