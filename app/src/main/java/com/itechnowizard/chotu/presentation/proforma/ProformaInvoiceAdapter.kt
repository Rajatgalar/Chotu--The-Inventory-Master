package com.itechnowizard.chotu.presentation.proforma

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.InvoiceListBinding
import com.itechnowizard.chotu.domain.model.InvoiceModel

class ProformaInvoiceAdapter(private val onPreviewClick: (InvoiceModel) -> Unit,
                             private val onDeleteClick: (Int) -> Unit,
                             private val onItemClick: (InvoiceModel,Int) -> Unit
)   : RecyclerView.Adapter<ProformaInvoiceAdapter.ViewHolder>(), Filterable {

    var invoicesList = mutableListOf<InvoiceModel>()

    private var filteredList :List<InvoiceModel> = invoicesList

    fun setInvoiceList(documentlist : List<InvoiceModel>) {
        this.invoicesList.clear()
        this.invoicesList.addAll(documentlist)
        filteredList = invoicesList
        //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: InvoiceListBinding) : RecyclerView.ViewHolder(binding.root) {
        //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: InvoiceModel, position: Int) {
            binding.tvInvoiceBuyerName.text = item.buyerDetail!!.companyName
            binding.tvInvoiceNumber.text = item.invoiceNumber
            binding.tvInvoiceDate.text = item.invoiceDate
            binding.tvInvoiceAmount.text =item.billFinalAmount.toString()
            binding.btnPreview.setOnClickListener { onPreviewClick(item) }
            itemView.setOnClickListener { onItemClick(item,position) }
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
                val filteredResults = invoicesList.filter {
                    it.buyerDetail!!.companyName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<InvoiceModel>)
                notifyDataSetChanged()
            }
        }
    }


}
