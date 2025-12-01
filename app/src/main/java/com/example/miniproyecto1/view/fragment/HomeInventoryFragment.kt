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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeInventoryFragment : Fragment() {

    private lateinit var binding: FragmentHomeInventoryBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    private lateinit var sessionManager: SessionManager

    // 🔹 Mantener un solo Adapter
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
    // 🔹 RECYCLERVIEW CONFIG
    // -------------------------------
    private fun setupRecyclerView() {
        adapter = InventoryAdapter(mutableListOf(), findNavController())

        binding.recyclerviewInventory.layoutManager =
            LinearLayoutManager(requireContext())

        binding.recyclerviewInventory.adapter = adapter
    }

    // -------------------------------
    // 🔹 OBSERVERS
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
    // 🔹 UI BUTTONS
    // -------------------------------
    private fun setupUI() {
        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }

        binding.ivLogout.setOnClickListener {
            sessionManager.clearSession()

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
    // 🔹 SESIÓN
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
    // 🔹 BACK BUTTON (SALIR DE LA APP)
    // -------------------------------
    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(true)
        }
    }
}