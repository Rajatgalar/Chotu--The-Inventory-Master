package com.itechnowizard.chotu.presentation.quotation

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.InvoiceListBinding
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.QuotationModel

class QuotationAdapter(private val onPreviewClick: (QuotationModel) -> Unit,
                       private val onDeleteClick: (Int) -> Unit,
                       private val onItemClick: (QuotationModel,Int) -> Unit
)   : RecyclerView.Adapter<QuotationAdapter.ViewHolder>(), Filterable {

    var quotationsList = mutableListOf<QuotationModel>()

    private var filteredList :List<QuotationModel> = quotationsList

    fun setQuotationList(documentlist : List<QuotationModel>) {
        this.quotationsList.clear()
        this.quotationsList.addAll(documentlist)
        filteredList = quotationsList
        //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: InvoiceListBinding) : RecyclerView.ViewHolder(binding.root) {
        //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: QuotationModel, position: Int) {
            binding.tvInvoiceBuyerName.text = item.buyerDetail!!.companyName
            binding.tvInvoiceNumber.text = item.invoiceNumber
            binding.tvInvoiceDate.text = item.invoiceDate
            binding.tvInvoiceAmount.text =item.billFinalAmount.toString()
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.btnPreview.setOnClickListener { onPreviewClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = InvoiceListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = quotationsList.filter {
                    it.buyerDetail!!.companyName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<QuotationModel>)
                notifyDataSetChanged()
            }
        }
    }


}
