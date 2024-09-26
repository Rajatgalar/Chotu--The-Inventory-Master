package com.itechnowizard.chotu.presentation.debitnote

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.DebitnoteListBinding
import com.itechnowizard.chotu.domain.model.CreditNoteModel
import com.itechnowizard.chotu.domain.model.DebitNoteModel

class DebitNoteAdapter(private val onPreviewClick: (DebitNoteModel) -> Unit,
                       private val onDeleteClick: (String,Int) -> Unit,
                       private val onItemClick: (DebitNoteModel,Int) -> Unit
)   : RecyclerView.Adapter<DebitNoteAdapter.ViewHolder>(), Filterable {

    var debitNotesList = mutableListOf<DebitNoteModel>()

    private var filteredList :List<DebitNoteModel> = debitNotesList

    fun setDebitNoteList(documentlist : List<DebitNoteModel>) {
        this.debitNotesList.clear()
        this.debitNotesList.addAll(documentlist)
        filteredList = debitNotesList
        //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: DebitnoteListBinding) : RecyclerView.ViewHolder(binding.root) {
        //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: DebitNoteModel, position: Int) {
            binding.tvDebitNoteBuyerName.text = item.sellerDetail!!.companyName
            binding.tvDebitNoteNumber.text = item.debitNoteNumber
            binding.tvDebitNoteDate.text = item.debitNoteDate
            binding.tvDebitNoteAmount.text =item.totalAmount
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.btnPreview.setOnClickListener { onPreviewClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(item.sellerId!!,position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DebitnoteListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = debitNotesList.filter {
                    it.debitNoteCode!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<DebitNoteModel>)
                notifyDataSetChanged()
            }
        }
    }


}
