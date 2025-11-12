package com.example.miniproyecto1.view.fragment

import android.app.AlertDialog
import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.miniproyecto1.R
import com.example.miniproyecto1.databinding.FragmentItemDetailsBinding
import com.example.miniproyecto1.model.Inventory
import com.example.miniproyecto1.viewmodel.InventoryViewModel
import java.util.Locale

class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private var productCode: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productCode = arguments?.getInt("code") ?: -1

        if (productCode == -1) {
            Toast.makeText(requireContext(), "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        setupToolbar()
        setupButtons()
        observeProduct()
        handleBackButton()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupButtons() {
        binding.btnDelete.setOnClickListener { confirmDelete() }
        binding.fabEdit.setOnClickListener {
            val bundle = Bundle().apply { putInt("code", productCode) }
            findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
        }
    }

    private fun observeProduct() {
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { list ->
            val product = list.find { it.code == productCode }
            product?.let { showProductDetails(it) }
        }
        inventoryViewModel.getListInventory()
    }

    private fun showProductDetails(product: Inventory) {
        val formatoColombiano = NumberFormat.getInstance(Locale.forLanguageTag("es-CO")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        val precioFormateado = "$ ${formatoColombiano.format(product.price)}"
        val totalFormateado = "$ ${formatoColombiano.format(product.price * product.quantity)}"

        binding.tvName.text = product.name
        binding.tvPrice.text = precioFormateado
        binding.tvQuantity.text = product.quantity.toString()
        binding.tvTotal.text = totalFormateado

        binding.toolbar.title = product.name
    }

    private fun confirmDelete() {
        val product = inventoryViewModel.listInventory.value?.find { it.code == productCode }
        if (product != null) {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Deseas eliminar ${product.name}?")
                .setPositiveButton("Sí") { _, _ ->
                    inventoryViewModel.deleteInventory(product)
                    Toast.makeText(requireContext(), "${product.name} eliminado", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    private fun handleBackButton() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }
}
