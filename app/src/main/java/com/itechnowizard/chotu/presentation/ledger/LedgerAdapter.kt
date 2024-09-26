package com.itechnowizard.chotu.presentation.ledger

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.InvoiceListBinding
import com.itechnowizard.chotu.databinding.LedgerListBinding
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import kotlinx.android.synthetic.main.ledger_list.view.*

class LedgerAdapter(
                    private val onItemClick: (CreditorDebitorModel,Int) -> Unit
)   : RecyclerView.Adapter<LedgerAdapter.ViewHolder>(), Filterable {

    private var invoicesList = mutableListOf<CreditorDebitorModel>()

    private var filteredList :List<CreditorDebitorModel> = invoicesList
    private var isBuyer = false

    fun setLedgerList(documentlist: List<CreditorDebitorModel>, isBuyer: Boolean) {
        this.isBuyer = isBuyer
        this.invoicesList.clear()
        this.invoicesList.addAll(documentlist)
        filteredList = invoicesList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: LedgerListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CreditorDebitorModel, position: Int) {

            binding.tvTitle.text = item.name!!

            val isReceivable = if (isBuyer) item.totalAmount!! >= 0 else item.totalAmount!! < 0
            binding.text.text = if (isReceivable) "Receivable" else "Payable"
            if (!isReceivable)
                binding.amount.setTextColor(itemView.context.getColor(R.color.red))
            else
                binding.amount.setTextColor(itemView.context.getColor(R.color.green))
            binding.amount.text = item.totalAmount.toString()

            itemView.setOnClickListener { onItemClick(item,position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LedgerListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = invoicesList.filter {
                    it.name!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<CreditorDebitorModel>)
                notifyDataSetChanged()
            }
        }
    }


}
