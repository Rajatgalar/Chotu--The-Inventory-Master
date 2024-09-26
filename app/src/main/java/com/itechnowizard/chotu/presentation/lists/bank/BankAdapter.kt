package com.itechnowizard.chotu.presentation.lists.bank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.BankListBinding
import com.itechnowizard.chotu.domain.model.BankModel
import com.itechnowizard.chotu.domain.model.InvoiceModel

class BankAdapter(
    private val onEditClick: (BankModel, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (BankModel) -> Unit
)
    : RecyclerView.Adapter<BankAdapter.ViewHolder>(), Filterable {

    var bankingList = mutableListOf<BankModel>()
    private var filteredList :List<BankModel> = bankingList


    fun setBankList(documentlist : List<BankModel>) {
        this.bankingList.clear()
        this.bankingList.addAll(documentlist)
        filteredList = bankingList
    //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: BankListBinding) : RecyclerView.ViewHolder(binding.root) {
     //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: BankModel, position: Int) {
            binding.tvBankName.text = item.bankName
            binding.tvIFSCCode.text = item.ifsccode
            if(!item.accountHolderName.isNullOrBlank()){
                binding.tvAccountHolderName.apply {
                    visibility = View.VISIBLE
                    text = item.accountHolderName
                }
            }

            itemView.setOnClickListener { onItemClick(item) }
            binding.btnEdit.setOnClickListener { onEditClick(item,position) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BankListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = bankingList.filter {
                    it.bankName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<BankModel>)
                notifyDataSetChanged()
            }
        }
    }

}
