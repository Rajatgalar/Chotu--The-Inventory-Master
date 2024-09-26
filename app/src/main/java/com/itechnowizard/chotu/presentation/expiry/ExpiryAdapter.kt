package com.itechnowizard.chotu.presentation.expiry

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.ExpiryListBinding
import com.itechnowizard.chotu.databinding.ProductListBinding
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.ExpiryModel
import com.itechnowizard.chotu.domain.model.ProductModel

class ExpiryAdapter()
    : RecyclerView.Adapter<ExpiryAdapter.ViewHolder>() , Filterable {

    var productList = mutableListOf<ExpiryModel>()

    private var filteredList :List<ExpiryModel> = productList

    fun setProductsList(documentlist : List<ExpiryModel>) {
        this.productList.clear()
        this.productList.addAll(documentlist)
        filteredList = productList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ExpiryListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ExpiryModel, position: Int) {
            binding.tvTitle.text = item.productName
            binding.tvExpiryDate.text = item.expiryDate.toString().ifBlank { "xx-xx-xxxx" }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ExpiryListBinding.inflate(inflater,parent,false)
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
                    it.productName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<ExpiryModel>)
                notifyDataSetChanged()
            }
        }
    }

}
