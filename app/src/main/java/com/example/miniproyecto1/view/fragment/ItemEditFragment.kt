package com.example.miniproyecto1.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.miniproyecto1.R
import com.example.miniproyecto1.databinding.FragmentItemEditBinding
import com.example.miniproyecto1.model.Inventory
import com.example.miniproyecto1.viewmodel.InventoryViewModel

class ItemEditFragment : Fragment() {

    private lateinit var binding: FragmentItemEditBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()

    private var productCode: Int = -1
    private var currentInventory: Inventory? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recibir código desde el fragment anterior
        productCode = arguments?.getInt("code") ?: -1

        if (productCode == -1) {
            Toast.makeText(requireContext(), "Error: producto no especificado", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
            return
        }

        configurarToolbar()
        configurarFiltrosYValidacion()
        observarViewModel()

        // Cargar datos
        inventoryViewModel.getListInventory()
    }

    private fun configurarToolbar() {
        binding.topAppBar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.topAppBar.setNavigationOnClickListener {
            val bundle = Bundle().apply { putInt("code", productCode) }
            findNavController().navigate(R.id.itemDetailsFragment, bundle)
        }
    }

    private fun configurarFiltrosYValidacion() {
        binding.etName.filters = arrayOf(InputFilter.LengthFilter(40))
        binding.etPrice.filters = arrayOf(InputFilter.LengthFilter(20))
        binding.etQuantity.filters = arrayOf(InputFilter.LengthFilter(4))

        binding.etPrice.keyListener = DigitsKeyListener.getInstance("0123456789")
        binding.etQuantity.keyListener = DigitsKeyListener.getInstance("0123456789")

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validarCampos()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.etName.addTextChangedListener(watcher)
        binding.etPrice.addTextChangedListener(watcher)
        binding.etQuantity.addTextChangedListener(watcher)

        binding.btnEdit.setOnClickListener {
            realizarEdicion()
        }
    }

    private fun validarCampos() {
        val name = binding.etName.text?.toString()?.trim() ?: ""
        val price = binding.etPrice.text?.toString()?.trim() ?: ""
        val quantity = binding.etQuantity.text?.toString()?.trim() ?: ""

        val nameValid = name.isNotEmpty() && name.length <= 40
        val priceValid = price.isNotEmpty() && price.matches(Regex("^[0-9]{1,20}$"))
        val quantityValid = quantity.isNotEmpty() && quantity.matches(Regex("^[0-9]{1,4}$"))

        binding.btnEdit.isEnabled = nameValid && priceValid && quantityValid
    }

    private fun observarViewModel() {
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { lista ->
            currentInventory = lista.find { it.code == productCode }

            if (currentInventory != null) {
                poblarDatos(currentInventory!!)
            } else {
                Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun poblarDatos(producto: Inventory) {
        binding.tvCode.text = "Id: ${producto.code}"
        binding.etName.setText(producto.name)
        binding.etPrice.setText(producto.price.toString())
        binding.etQuantity.setText(producto.quantity.toString())
        validarCampos()
    }

    private fun realizarEdicion() {
        val producto = currentInventory ?: run {
            Toast.makeText(requireContext(), "Error: producto no disponible", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevoNombre = binding.etName.text.toString().trim()
        val nuevoPrecioStr = binding.etPrice.text.toString().trim()
        val nuevaCantidadStr = binding.etQuantity.text.toString().trim()

        val nuevoPrecio = nuevoPrecioStr.toIntOrNull()
        val nuevaCantidad = nuevaCantidadStr.toIntOrNull()

        if (nuevoPrecio == null) {
            Toast.makeText(requireContext(), "Precio inválido", Toast.LENGTH_SHORT).show()
            return
        }
        if (nuevaCantidad == null) {
            Toast.makeText(requireContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val actualizado = Inventory(
            id = producto.id,
            code = producto.code,
            name = nuevoNombre,
            price = nuevoPrecio,
            quantity = nuevaCantidad
        )

        inventoryViewModel.updateInventory(actualizado)
        Toast.makeText(requireContext(), "Producto actualizado", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_itemEditFragment_to_homeInventoryFragment)
    }
}
