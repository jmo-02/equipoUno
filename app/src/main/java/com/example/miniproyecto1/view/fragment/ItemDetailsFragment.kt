package com.example.miniproyecto1.view.fragment

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
import com.example.miniproyecto1.viewmodel.InventoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.util.Locale

@AndroidEntryPoint
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

        recibirCodigoProducto()

        if (productCode == -1) {
            mostrarErrorProductoNoEncontrado()
            return
        }

        configurarInterfazUsuario()
        observarViewModel()
        configurarBotonAtras()
    }

    private fun recibirCodigoProducto() {
        productCode = arguments?.getInt("code") ?: -1
    }

    private fun mostrarErrorProductoNoEncontrado() {
        Toast.makeText(requireContext(), "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    private fun configurarInterfazUsuario() {
        binding.toolbar.setNavigationOnClickListener { regresarAListaProductos() }
        binding.btnDelete.setOnClickListener { mostrarDialogoConfirmacionEliminar() }
        binding.fabEdit.setOnClickListener { navegarAEdicionProducto() }
    }

    private fun observarViewModel() {
        inventoryViewModel.listInventory.observe(viewLifecycleOwner) { listaProductos ->
            buscarYMostrarProducto(listaProductos)
        }
        inventoryViewModel.getListInventory()
    }

    private fun buscarYMostrarProducto(listaProductos: MutableList<com.example.miniproyecto1.model.Inventory>) {
        val producto = listaProductos.find { it.code == productCode }
        if (producto != null) mostrarDatosProducto(producto)
        else mostrarErrorProductoNoEncontrado()
    }

    private fun mostrarDatosProducto(producto: com.example.miniproyecto1.model.Inventory) {
        val totalProducto = producto.price * producto.quantity

        // 🔹 Formato de número colombiano
        val formatoColombiano = NumberFormat.getInstance(Locale("es", "CO")).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        val precioFormateado = formatoColombiano.format(producto.price)
        val totalFormateado = formatoColombiano.format(totalProducto)

        // 🔹 Asignar a las vistas
        binding.tvNameValue.text = producto.name
        binding.tvPriceValue.text = "$ $precioFormateado"
        binding.tvQuantityValue.text = producto.quantity.toString()
        binding.tvTotalValue.text = "$ $totalFormateado"
    }

    private fun mostrarDialogoConfirmacionEliminar() {
        val productoEliminar = obtenerProductoActual()
        if (productoEliminar != null) {
            android.app.AlertDialog.Builder(requireContext())
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar ${productoEliminar.name}?")
                .setPositiveButton("Sí") { _, _ -> eliminarProducto(productoEliminar) }
                .setNegativeButton("No", null)
                .show()
        } else {
            Toast.makeText(requireContext(), "Error: Producto no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerProductoActual(): com.example.miniproyecto1.model.Inventory? {
        val listaActual = inventoryViewModel.listInventory.value
        return listaActual?.find { it.code == productCode }
    }

    private fun eliminarProducto(producto: com.example.miniproyecto1.model.Inventory) {
        inventoryViewModel.deleteInventory(producto)
        Toast.makeText(requireContext(), "${producto.name} eliminado", Toast.LENGTH_SHORT).show()
        regresarAListaProductos()
    }

    private fun navegarAEdicionProducto() {
        val bundle = Bundle().apply { putInt("code", productCode) }
        findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment, bundle)
    }

    private fun regresarAListaProductos() {
        findNavController().navigateUp()
    }

    private fun configurarBotonAtras() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            regresarAListaProductos()
        }
    }
}
