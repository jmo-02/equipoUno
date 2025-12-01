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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
            // En lugar de crear una nueva instancia de ItemDetailsFragment, simplemente
            // volvemos al fragmento anterior en la pila para evitar ciclos de navegación.
            findNavController().navigateUp()
        }
    }

    private fun configurarFiltrosYValidacion() {
        // Asegurar que el botón esté deshabilitado hasta que los campos sean válidos (Criterio 5)
        binding.btnEdit.isEnabled = false

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

        // Validaciones Criterios 5-8
        if (name.isEmpty()) {
            binding.tilName.error = "El nombre no puede estar vacío"
        } else if (name.length > 40) {
            binding.tilName.error = "Máximo 40 caracteres"
        } else {
            binding.tilName.error = null
        }

        if (price.isEmpty()) {
            binding.tilPrice.error = "El precio no puede estar vacío"
        } else if (!price.matches(Regex("^[0-9]{1,20}$"))) {
            binding.tilPrice.error = "Precio inválido (solo números, hasta 20 dígitos)"
        } else {
            binding.tilPrice.error = null
        }

        if (quantity.isEmpty()) {
            binding.tilQuantity.error = "La cantidad no puede estar vacía"
        } else if (!quantity.matches(Regex("^[0-9]{1,4}$"))) {
            binding.tilQuantity.error = "Cantidad inválida (solo números, hasta 4 dígitos)"
        } else {
            binding.tilQuantity.error = null
        }

        // Usar la función reutilizable para determinar si el formulario es válido
        binding.btnEdit.isEnabled = isFormValid()
    }

    // Función reusable para evaluar el estado del formulario sin cambiar UI (útil para tests)
    private fun isFormValid(): Boolean {
        val name = binding.etName.text?.toString()?.trim() ?: ""
        val price = binding.etPrice.text?.toString()?.trim() ?: ""
        val quantity = binding.etQuantity.text?.toString()?.trim() ?: ""

        if (name.isEmpty() || name.length > 40) return false
        if (!price.matches(Regex("^[0-9]{1,20}$"))) return false
        if (!quantity.matches(Regex("^[0-9]{1,4}$"))) return false

        return true
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
        // Mostrar el código como "Id: <codigo>" directamente para evitar problemas con recursos en tiempo de compilación
        // Usar recurso de string con placeholder para internacionalización (evita advertencias Lint)
        binding.tvCode.text = getString(R.string.label_id, producto.code)
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
