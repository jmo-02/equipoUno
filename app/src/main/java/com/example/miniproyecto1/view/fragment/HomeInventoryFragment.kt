package com.example.miniproyecto1.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miniproyecto1.R
import com.example.miniproyecto1.databinding.FragmentHomeInventoryBinding
import com.example.miniproyecto1.utils.SessionManager
import com.example.miniproyecto1.view.adapter.InventoryAdapter
import com.example.miniproyecto1.viewmodel.InventoryViewModel
import com.example.miniproyecto1.widget.InventoryWidgetProvider
import com.google.firebase.auth.FirebaseAuth
import java.text.NumberFormat
import java.util.Locale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeInventoryFragment : Fragment() {

    private lateinit var binding: FragmentHomeInventoryBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var sessionManager: SessionManager
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeInventoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        sessionManager = SessionManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupUI()
        observeViewModel()
        handleBackPressed()
    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter(mutableListOf(), findNavController())
        binding.recyclerviewInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewInventory.adapter = adapter
    }

    private fun observeViewModel() {
        inventoryViewModel.getListInventory()

        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)

            // Calcular total y cachearlo para el widget
            val total = list.fold(0.0) { acc, item ->
                acc + (item.price.toDouble() * item.quantity)
            }
            cacheWidgetTotal(requireContext(), total)
            // Opcional: actualizar los widgets cuando cambie el total
            InventoryWidgetProvider.updateAllWidgets(requireContext())
        }

        inventoryViewModel.progressState.observe(viewLifecycleOwner) { state ->
            binding.progressLoading.isVisible = state
        }
    }

    private fun setupUI() {
        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }

        binding.ivLogout.setOnClickListener {
            // Cerrar sesión real y limpiar cache del widget
            FirebaseAuth.getInstance().signOut()
            sessionManager.clearSession()
            clearWidgetTotalCache(requireContext())
            InventoryWidgetProvider.clearAllVisibilityPrefs(requireContext())

            findNavController().navigate(
                R.id.action_homeInventoryFragment_to_loginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
        }
    }

    private fun checkSession() {
        if (!sessionManager.isLoggedIn()) {
            findNavController().navigate(
                R.id.action_homeInventoryFragment_to_loginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
        }
    }

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(true)
        }
    }

    // Cache helpers
    private fun cacheWidgetTotal(context: Context, total: Double) {
        val formatted = formatMoney(total)
        val prefs = context.getSharedPreferences("inventory_widget_cache", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("total_formatted", formatted) // guardamos formateado para mostrar rápido
            .putLong("total_updated_at", System.currentTimeMillis())
            .apply()
    }

    private fun clearWidgetTotalCache(context: Context) {
        val prefs = context.getSharedPreferences("inventory_widget_cache", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    private fun formatMoney(value: Double): String {
        val locale = Locale("es", "ES")
        val nf = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            isGroupingUsed = true
        }
        return nf.format(value)
    }
}