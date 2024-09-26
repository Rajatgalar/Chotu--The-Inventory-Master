package com.itechnowizard.chotu.presentation.ledger.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.DetailledgerListBinding
import com.itechnowizard.chotu.databinding.InvoiceListBinding
import com.itechnowizard.chotu.databinding.LedgerListBinding
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import kotlinx.android.synthetic.main.ledger_list.view.*

class DetailLedgerAdapter(
                    private val onItemClick: (Int) -> Unit
)   : RecyclerView.Adapter<DetailLedgerAdapter.ViewHolder>() {

    var invoicesList = mutableListOf<BuyerSellerLedgerModel>()

    fun setLedgerList(documentlist : List<BuyerSellerLedgerModel>) {
        this.invoicesList.clear()
        this.invoicesList.addAll(documentlist)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: DetailledgerListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BuyerSellerLedgerModel, position: Int) {

           binding.tvTitle.text = item.type
           binding.tvNumber.text = "#"+item.invoiceNumber
           binding.tvDate.text = item.date
            binding.amount.text = item.totalAmount.toString()

            itemView.setOnClickListener { onItemClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DetailledgerListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(invoicesList[position],position)
    }

    override fun getItemCount() = invoicesList.size


}
