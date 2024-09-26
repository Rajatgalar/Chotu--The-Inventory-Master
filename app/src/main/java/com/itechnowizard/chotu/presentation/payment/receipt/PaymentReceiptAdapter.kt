package com.itechnowizard.chotu.presentation.payment.receipt

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.PaymentReceiptBinding
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.PaymentReceiptModel

class PaymentReceiptAdapter(private val onPreviewClick: (PaymentReceiptModel) -> Unit,
                            private val onDeleteClick: (Int,String) -> Unit,
                            private val onItemClick: (PaymentReceiptModel,Int) -> Unit
)   : RecyclerView.Adapter<PaymentReceiptAdapter.ViewHolder>(), Filterable {

    var receiptsList = mutableListOf<PaymentReceiptModel>()

    private var filteredList :List<PaymentReceiptModel> = receiptsList

    fun setPaymentReceiptList(documentlist : List<PaymentReceiptModel>) {
        this.receiptsList.clear()
        this.receiptsList.addAll(documentlist)
        filteredList = receiptsList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: PaymentReceiptBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PaymentReceiptModel, position: Int) {
            binding.tvPaymentReceiptBuyerName.text = item.buyerName
            binding.PaymentReceiptNumber.text = "Receipt No. : ${item.receiptNumber}"
            binding.PaymentReceiptDate.text = item.paymentDate
            binding.tvPaymentReceiptMode.text = item.paymentMode
            binding.PaymentTreatment.text = "${item.treatment}"
            binding.tvPaymentReceiptAmount.text = item.totalAmount.toString()
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.btnPreview.setOnClickListener { onPreviewClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position,item.buyerId!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PaymentReceiptBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = receiptsList.filter {
                    it.buyerName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<PaymentReceiptModel>)
                notifyDataSetChanged()
            }
        }
    }


}
