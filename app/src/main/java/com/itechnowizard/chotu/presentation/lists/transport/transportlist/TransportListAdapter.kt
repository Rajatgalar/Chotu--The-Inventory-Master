package com.itechnowizard.chotu.presentation.lists.transport.transportlist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.BuyerListBinding
import com.itechnowizard.chotu.databinding.TransportListBinding
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.TransportListModel

class TransportListAdapter(
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (TransportListModel) -> Unit
)
    : RecyclerView.Adapter<TransportListAdapter.ViewHolder>(),Filterable {

    var buyerList = mutableListOf<TransportListModel>()
    private var filteredList :List<TransportListModel> = buyerList


    fun setBuyersList(documentlist : List<TransportListModel>) {
        this.buyerList.clear()
        this.buyerList.addAll(documentlist)
        filteredList = buyerList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: TransportListBinding) : RecyclerView.ViewHolder(binding.root) {
     //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: TransportListModel, position: Int) {
            binding.tvTransportName.text = item.name
            binding.tvTransportId.text = item.transportId
            itemView.setOnClickListener { onItemClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TransportListBinding.inflate(inflater,parent,false)
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
                    it.name!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<TransportListModel>)
                notifyDataSetChanged()
            }
        }
    }

}
