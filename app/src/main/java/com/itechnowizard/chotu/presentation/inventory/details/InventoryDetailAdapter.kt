package com.itechnowizard.chotu.presentation.inventory.details

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.InventoryDetailListBinding
import com.itechnowizard.chotu.databinding.InventoryListBinding
import com.itechnowizard.chotu.domain.model.InventoryModel

class InventoryDetailAdapter() : RecyclerView.Adapter<InventoryDetailAdapter.ViewHolder>() {

    var inventoryList = mutableListOf<InventoryModel>()

    fun setInventorysList(documentlist: List<InventoryModel>) {
        this.inventoryList.clear()
        this.inventoryList.addAll(documentlist)
        //    this.inventoryList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: InventoryDetailListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: InventoryModel, position: Int) {

            binding.tvNumber.text = "#${item.sno}"
            binding.sellbuy.text = item.remark
            binding.date.text = item.date
            binding.changeStock.text = item.stock.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = InventoryDetailListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(inventoryList[position], position)
    }

    override fun getItemCount() = inventoryList.size

}
