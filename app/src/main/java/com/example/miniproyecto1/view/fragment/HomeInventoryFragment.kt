package com.example.miniproyecto1.view.fragment

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

    // -------------------------------
    // RecyclerView
    // -------------------------------
    private fun setupRecyclerView() {
        adapter = InventoryAdapter(mutableListOf(), findNavController())
        binding.recyclerviewInventory.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewInventory.adapter = adapter
    }

    // -------------------------------
    // Observers
    // -------------------------------
    private fun observeViewModel() {
        inventoryViewModel.getListInventory()
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }
        inventoryViewModel.progressState.observe(viewLifecycleOwner) { state ->
            binding.progressLoading.isVisible = state
        }
    }

    // -------------------------------
    // UI Buttons
    // -------------------------------
    private fun setupUI() {
        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }

        binding.ivLogout.setOnClickListener {
            // 1) Cerrar sesión de Firebase (clave para que el widget detecte que no hay usuario)
            FirebaseAuth.getInstance().signOut()

            // 2) Limpiar sesión local
            sessionManager.clearSession()

            // 3) Limpiar visibilidad del widget y refrescar todos los widgets
            InventoryWidgetProvider.clearAllVisibilityPrefs(requireContext())

            // 4) Navegar a Login y limpiar el back stack
            findNavController().navigate(
                R.id.action_homeInventoryFragment_to_loginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
        }
    }

    // -------------------------------
    // Sesión
    // -------------------------------
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

    // -------------------------------
    // Botón atrás -> salir de la app
    // -------------------------------
    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(true)
        }
    }
}