package com.itechnowizard.chotu.presentation.lists.seller

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.SellerListBinding
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.SellerModel

class SellerAdapter (
    private val onEditClick: (SellerModel, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (SellerModel, Int) -> Unit
) : RecyclerView.Adapter<SellerAdapter.ViewHolder>() , Filterable {

    var sellerList = mutableListOf<SellerModel>()
    private var filteredList :List<SellerModel> = sellerList


    fun setSellersList(documentlist : List<SellerModel>) {
        this.sellerList.clear()
        this.sellerList.addAll(documentlist)
        filteredList = sellerList
        //    this.sellerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: SellerListBinding) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: SellerModel, position: Int) {
            binding.tvTitle.text = item.companyName
            binding.tvAddress.text = item.address
            binding.tvGSTIN.text = item.gstin
            binding.tvState.text =item.state
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.btnEdit.setOnClickListener { onEditClick(item,position) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SellerListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = sellerList.filter {
                    it.companyName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<SellerModel>)
                notifyDataSetChanged()
            }
        }
    }

}
