package com.itechnowizard.chotu.presentation.lists.buyer

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.BuyerListBinding
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.InvoiceModel

class BuyerAdapter(
    private val onEditClick: (BuyerModel, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (BuyerModel,Int) -> Unit
)
    : RecyclerView.Adapter<BuyerAdapter.ViewHolder>(), Filterable {

    var buyerList = mutableListOf<BuyerModel>()
    private var filteredList :List<BuyerModel> = buyerList


    fun setBuyersList(documentlist : List<BuyerModel>) {
        this.buyerList.clear()
        this.buyerList.addAll(documentlist)
        filteredList = buyerList
    //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: BuyerListBinding) : RecyclerView.ViewHolder(binding.root) {
     //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: BuyerModel, position: Int) {
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
        val binding = BuyerListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = buyerList.filter {
                    it.companyName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<BuyerModel>)
                notifyDataSetChanged()
            }
        }
    }

}
