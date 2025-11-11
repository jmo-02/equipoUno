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
    private lateinit var sessionManager: SessionManager

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
        setupUI()
        observeViewModel()
        handleBackPressed()
    }

    // Verifica sesión cada vez que el fragment vuelve a ser visible
    override fun onResume() {
        super.onResume()
        checkSessionAndRedirect()
    }

    private fun checkSessionAndRedirect() {
        if (!sessionManager.isLoggedIn()) {
            // Si no hay sesión, redirige al login y limpia el back stack
            findNavController().navigate(
                R.id.action_homeInventoryFragment_to_loginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true) // Limpia TODO el back stack
                    .build()
            )
        }
    }

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
                    .setPopUpTo(R.id.nav_graph, true) // Limpia TODO el back stack
                    .build()
            )
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

    private fun handleBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().moveTaskToBack(true)
        }
    }
}