package com.itechnowizard.chotu.presentation.creditnote

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.CreditnoteListBinding
import com.itechnowizard.chotu.domain.model.CreditNoteModel

class CreditNoteAdapter(private val onPreviewClick: (CreditNoteModel) -> Unit,
                       private val onDeleteClick: (String,Int) -> Unit,
                       private val onItemClick: (CreditNoteModel,Int) -> Unit
)   : RecyclerView.Adapter<CreditNoteAdapter.ViewHolder>(), Filterable {

    var creditNotesList = mutableListOf<CreditNoteModel>()

    private var filteredList :List<CreditNoteModel> = creditNotesList

    fun setCreditNoteList(documentlist : List<CreditNoteModel>) {
        this.creditNotesList.clear()
        this.creditNotesList.addAll(documentlist)
        filteredList = creditNotesList
        //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: CreditnoteListBinding) : RecyclerView.ViewHolder(binding.root) {
        //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: CreditNoteModel, position: Int) {
            binding.tvCreditNoteBuyerName.text = item.buyerDetail!!.companyName
            binding.tvCreditNoteNumber.text = item.creditNoteNumber
            binding.tvCreditNoteDate.text = item.creditNoteDate
            binding.tvCreditNoteAmount.text =item.totalAmount
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.btnPreview.setOnClickListener { onPreviewClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item.buyerId!!,position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CreditnoteListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = creditNotesList.filter {
                    it.creditNoteCode!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<CreditNoteModel>)
                notifyDataSetChanged()
            }
        }
    }


}
