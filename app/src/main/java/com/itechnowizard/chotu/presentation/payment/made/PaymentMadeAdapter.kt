package com.itechnowizard.chotu.presentation.payment.made

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.PaymentMadeBinding
import com.itechnowizard.chotu.domain.model.PaymentMadeModel
import com.itechnowizard.chotu.domain.model.PaymentReceiptModel

class PaymentMadeAdapter(private val onPreviewClick: (PaymentMadeModel) -> Unit,
                         private val onDeleteClick: (Int,String) -> Unit,
                         private val onItemClick: (PaymentMadeModel,Int) -> Unit
)   : RecyclerView.Adapter<PaymentMadeAdapter.ViewHolder>(), Filterable {

    var receiptsList = mutableListOf<PaymentMadeModel>()

    private var filteredList :List<PaymentMadeModel> = receiptsList

    fun setPaymentMadeList(documentlist : List<PaymentMadeModel>) {
        this.receiptsList.clear()
        this.receiptsList.addAll(documentlist)
        filteredList = receiptsList
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: PaymentMadeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PaymentMadeModel, position: Int) {
            binding.tvPaymentMadeBuyerName.text = item.sellerName
            binding.PaymentMadeNumber.text = "Receipt No. : ${item.receiptNumber}"
            binding.PaymentMadeDate.text = item.paymentDate
            binding.tvPaymentMadeMode.text = item.paymentMode
            binding.PaymentTreatment.text = "${item.treatment}"
            binding.tvPaymentMadeAmount.text = item.totalAmount.toString()
            itemView.setOnClickListener { onItemClick(item,position) }
            binding.btnPreview.setOnClickListener { onPreviewClick(item) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position,item.sellerId!!) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PaymentMadeBinding.inflate(inflater,parent,false)
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
                    it.sellerName!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<PaymentMadeModel>)
                notifyDataSetChanged()
            }
        }
    }


}
