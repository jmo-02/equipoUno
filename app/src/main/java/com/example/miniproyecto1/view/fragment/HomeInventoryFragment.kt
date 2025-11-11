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

class HomeInventoryFragment : Fragment() {

    private lateinit var binding: FragmentHomeInventoryBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeInventoryBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        handleBackPressed()
    }

    private fun setupUI() {
        // 🔹 FAB: ir a HU 4.0 (Agregar producto)
        binding.fabAgregar.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }

        // 🔹 Logout: limpia sesión y vuelve al login
        binding.ivLogout.setOnClickListener {
            val session = SessionManager(requireContext())
            session.clearSession()
            findNavController().navigate(R.id.action_homeInventoryFragment_to_loginFragment)
        }
    }

    private fun observeViewModel() {
        inventoryViewModel.getListInventory()

        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { listInventory ->
            val recycler = binding.recyclerviewInventory
            recycler.layoutManager = LinearLayoutManager(context)
            recycler.adapter = InventoryAdapter(listInventory, findNavController())
        }

        inventoryViewModel.progressState.observe(viewLifecycleOwner) { status ->
            binding.progressLoading.isVisible = status
        }
    }

    // 🔹 Evita que el botón atrás del celular regrese al login
    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(true)
        }
    }
}
