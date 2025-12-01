package com.example.miniproyecto1.view.adapter

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproyecto1.R
import com.example.miniproyecto1.databinding.ItemInventoryBinding
import com.example.miniproyecto1.model.Inventory
import java.util.Locale
import android.os.Bundle

class InventoryAdapter(
    private val listInventory: MutableList<Inventory>,
    private val navController: NavController
) : RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder>() {

    fun updateData(newList: MutableList<Inventory>) {
        listInventory.clear()
        listInventory.addAll(newList)
        notifyDataSetChanged()


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InventoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = listInventory[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply { putInt("code", item.code) }
            navController.navigate(R.id.action_homeInventoryFragment_to_itemDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int = listInventory.size

    class InventoryViewHolder(private val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(inventory: Inventory) {
            binding.tvName.text = inventory.name

            val formatter = NumberFormat
                .getInstance(Locale.forLanguageTag("es-CO"))
                .apply {
                    minimumFractionDigits = 2
                    maximumFractionDigits = 2
                }

            binding.tvPrice.text = "$ ${formatter.format(inventory.price)}"
            binding.tvCode.text = "Id: ${inventory.code}"
        }
    }
}