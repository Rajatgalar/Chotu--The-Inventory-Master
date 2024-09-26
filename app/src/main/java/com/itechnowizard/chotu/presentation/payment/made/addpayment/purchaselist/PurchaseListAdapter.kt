package com.itechnowizard.chotu.presentation.payment.made.addpayment.purchaselist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.*
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel

class PurchaseListAdapter(
    private val onItemClick: (BuyerSellerLedgerModel, Int) -> Unit
)
    : RecyclerView.Adapter<PurchaseListAdapter.ViewHolder>(),Filterable {

    var buyerList = mutableListOf<BuyerSellerLedgerModel>()
    private var filteredList :List<BuyerSellerLedgerModel> = buyerList


    fun setSellersList(documentlist : List<BuyerSellerLedgerModel>) {
        this.buyerList.clear()
        this.buyerList.addAll(documentlist)
        filteredList = buyerList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: PaymentReceiptListBinding) : RecyclerView.ViewHolder(binding.root) {
     //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: BuyerSellerLedgerModel, position: Int) {
            binding.tvInvoiceNumber.text = item.invoiceNumber
            binding.tvInvoiceAmount.text = "Total Amount : ${item.totalAmount}"
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.tvDate.text = item.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PaymentReceiptListBinding.inflate(inflater,parent,false)
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
                    it.invoiceNumber!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<BuyerSellerLedgerModel>)
                notifyDataSetChanged()
            }
        }
    }

}
