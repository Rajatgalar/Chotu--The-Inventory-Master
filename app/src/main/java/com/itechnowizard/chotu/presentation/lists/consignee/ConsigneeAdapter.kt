package com.itechnowizard.chotu.presentation.lists.consignee

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.BuyerListBinding
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.ConsigneeModel
import com.itechnowizard.chotu.domain.model.InvoiceModel

class ConsigneeAdapter(
    private val onEditClick: (ConsigneeModel, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (ConsigneeModel) -> Unit
)
    : RecyclerView.Adapter<ConsigneeAdapter.ViewHolder>(), Filterable {

    var buyerList = mutableListOf<ConsigneeModel>()
    private var filteredList :List<ConsigneeModel> = buyerList


    fun setBuyersList(documentlist : List<ConsigneeModel>) {
        this.buyerList.clear()
        this.buyerList.addAll(documentlist)
        filteredList = buyerList
    //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: BuyerListBinding) : RecyclerView.ViewHolder(binding.root) {
     //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: ConsigneeModel, position: Int) {
            binding.tvTitle.text = item.companyName
            binding.tvAddress.text = item.address
            binding.tvGSTIN.text = item.gstin
            binding.tvState.text =item.state
            itemView.setOnClickListener { onItemClick(item) }
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
                filteredList = (results?.values as List<ConsigneeModel>)
                notifyDataSetChanged()
            }
        }
    }

}
