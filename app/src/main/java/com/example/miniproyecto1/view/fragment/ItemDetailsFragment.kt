package com.example.miniproyecto1.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.miniproyecto1.databinding.FragmentItemDetailsBinding

class ItemDetailsFragment : Fragment() {

    private lateinit var binding: FragmentItemDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemDetailsBinding.inflate(inflater, container, false)

        // 🟠 Recuperar el código que se envió desde el adapter
        val productCode = arguments?.getInt("productCode")

        // 🟠 Solo como ejemplo, mostrarlo temporalmente
        binding.textView.text = "Detalles del producto con código: $productCode"

        return binding.root
    }
}
