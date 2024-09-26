package com.itechnowizard.chotu.presentation.lists.product

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.ProductListBinding
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.ProductModel

class ProductAdapter(
    private val onEditClick: (ProductModel, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (ProductModel, Int) -> Unit
)
    : RecyclerView.Adapter<ProductAdapter.ViewHolder>() , Filterable {

    var productList = mutableListOf<ProductModel>()

    private var filteredList :List<ProductModel> = productList

    fun setProductsList(documentlist : List<ProductModel>) {
        this.productList.clear()
        this.productList.addAll(documentlist)
        filteredList = productList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ProductListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductModel, position: Int) {
            binding.tvTitle.text = item.itemName
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.btnEdit.setOnClickListener { onEditClick(item,position) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = productList.filter {
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
