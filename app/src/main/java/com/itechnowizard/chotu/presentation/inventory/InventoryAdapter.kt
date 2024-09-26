package com.itechnowizard.chotu.presentation.inventory

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.InventoryListBinding
import com.itechnowizard.chotu.domain.model.ProductModel

class InventoryAdapter(
    private val onEditClick: (ProductModel, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (ProductModel,Int) -> Unit
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>(), Filterable {

    var inventoryList = mutableListOf<ProductModel>()
    private var filteredList: List<ProductModel> = inventoryList


    fun setInventorysList(documentlist: List<ProductModel>) {
        this.inventoryList.clear()
        this.inventoryList.addAll(documentlist)
        filteredList = inventoryList
        //    this.inventoryList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: InventoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: ProductModel, position: Int) {

            binding.tvTitle.text = item.itemName
            binding.tvStockQty.text = item.totalStock.toString()
            binding.tvStockValue.text = (item.purchasePrice.toString().ifEmpty { "0" }.toDouble() * item.totalStock!!).toString()
            binding.tvPurchasePrice.text = item.purchasePrice.toString().ifBlank { "-" }
            binding.tvSalePrice.text = item.salePrice.toString().ifBlank { "-" }
            itemView.setOnClickListener { onItemClick(item,position) }
//            binding.btnEdit.setOnClickListener { onEditClick(item,position) }
//            binding.btnDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = InventoryListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position], position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = inventoryList.filter {
                    it.itemName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<ProductModel>)
                notifyDataSetChanged()
            }
        }
    }

}
