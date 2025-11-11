package com.example.miniproyecto1.view.fragment

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.miniproyecto1.R
import com.example.miniproyecto1.databinding.FragmentAddItemBinding
import com.example.miniproyecto1.model.Inventory
import com.example.miniproyecto1.viewmodel.InventoryViewModel
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class AddItemFragment : Fragment() {

    private lateinit var binding: FragmentAddItemBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddItemBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = inventoryViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupInputLimits()
        setupListeners()
    }

    /** Configura la flecha del Toolbar para volver al HomeInventario */
    private fun setupToolbar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_addItemFragment_to_homeInventoryFragment)
        }
    }

    /** Aplica límites de caracteres a los campos */
    private fun setupInputLimits() {
        binding.etName.filters = arrayOf(InputFilter.LengthFilter(40))
        binding.etPrice.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.etQuantity.filters = arrayOf(InputFilter.LengthFilter(4))
        binding.etCode.filters = arrayOf(InputFilter.LengthFilter(4))
    }

    /** Configura validaciones y botón guardar */
    private fun setupListeners() {
        validateInputs()

        binding.btnSaveInventory.setOnClickListener {
            val code = binding.etCode.text.toString().toIntOrNull() ?: 0
            val name = binding.etName.text.toString()
            val price = binding.etPrice.text.toString().toIntOrNull() ?: 0
            val quantity = binding.etQuantity.text.toString().toIntOrNull() ?: 0

            if (code <= 0 || name.isEmpty() || price <= 0 || quantity <= 0) {
                Toast.makeText(context, "Por favor completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val inventory = Inventory(code = code, name = name, price = price, quantity = quantity)

            // 🔹 Ejecutar dentro de una corutina
            viewLifecycleOwner.lifecycleScope.launch {
                val result = inventoryViewModel.saveInventory(inventory)

                if (result) {
                    Toast.makeText(context, "Producto guardado correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_addItemFragment_to_homeInventoryFragment)
                } else {
                    Toast.makeText(context, "Error: código de producto ya existe", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    /**  Valida campos en tiempo real (botón solo activo si todo está lleno */
    private fun validateInputs() {
        val fields = listOf(binding.etCode, binding.etName, binding.etPrice, binding.etQuantity)

        for (field in fields) {
            field.addTextChangedListener {
                val allFilled = fields.all { it.text?.isNotEmpty() == true }
                binding.btnSaveInventory.isEnabled = allFilled
            }
        }

        binding.btnSaveInventory.isEnabled = false
    }
}
