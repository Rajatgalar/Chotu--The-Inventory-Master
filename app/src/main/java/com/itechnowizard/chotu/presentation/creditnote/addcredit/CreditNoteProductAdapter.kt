package com.itechnowizard.chotu.presentation.creditnote.addcredit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.InvoiceProductListBinding
import com.itechnowizard.chotu.domain.model.SelectedProductModel

class CreditNoteProductAdapter(
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (SelectedProductModel) -> Unit
)
    : RecyclerView.Adapter<CreditNoteProductAdapter.ViewHolder>() {

    private var productList = mutableListOf<SelectedProductModel>()

    fun setCreditNoteProductList(list : List<SelectedProductModel>) {
        this.productList.clear()
        this.productList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: InvoiceProductListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SelectedProductModel, position: Int) {
            binding.tvTitle.text = item.itemName
            itemView.setOnClickListener { onItemClick(item) }
//            binding.btnEdit.setOnClickListener { onEditClick(item,position) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position) }
            binding.tvQuantity.text = item.quantity + " X "+ item.unitPrice
            binding.tvAmount.text = item.amount.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = InvoiceProductListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position],position)
    }

    override fun getItemCount() = productList.size
}
