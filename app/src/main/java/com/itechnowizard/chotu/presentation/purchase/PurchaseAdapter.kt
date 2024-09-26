package com.itechnowizard.chotu.presentation.purchase

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.PurchaseListBinding
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.PurchaseModel

class PurchaseAdapter (private val onPreviewClick: (PurchaseModel) -> Unit,
                       private val onDeleteClick: (Int,String) -> Unit,
                       private val onItemClick: (PurchaseModel, Int) -> Unit
)   : RecyclerView.Adapter<PurchaseAdapter.ViewHolder>(), Filterable {

    var purchasesList = mutableListOf<PurchaseModel>()
    private var filteredList :List<PurchaseModel> = purchasesList


    fun setPurchaseList(documentlist : List<PurchaseModel>) {
        this.purchasesList.clear()
        this.purchasesList.addAll(documentlist)
        filteredList = purchasesList
        //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: PurchaseListBinding) : RecyclerView.ViewHolder(binding.root) {
        //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: PurchaseModel, position: Int) {
            binding.tvPurchaseBuyerName.text = item.sellerDetail!!.companyName
            binding.tvPurchaseNumber.text = item.purchaseNumber
            binding.tvPurchaseDate.text = item.purchaseDate
            binding.tvPurchaseAmount.text =item.billFinalAmount.toString()
            binding.btnPreview.setOnClickListener { onPreviewClick(item) }
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position,item.sellerId!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PurchaseListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = purchasesList.filter {
                    it.sellerDetail!!.companyName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<PurchaseModel>)
                notifyDataSetChanged()
            }
        }
    }

}
