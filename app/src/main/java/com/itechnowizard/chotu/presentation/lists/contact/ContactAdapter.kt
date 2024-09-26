package com.itechnowizard.chotu.presentation.lists.contact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.databinding.ContactListBinding
import com.itechnowizard.chotu.domain.model.ContactModel
import com.itechnowizard.chotu.domain.model.InvoiceModel

class ContactAdapter(
    private val onEditClick: (ContactModel, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onItemClick: (ContactModel) -> Unit
)
    : RecyclerView.Adapter<ContactAdapter.ViewHolder>(), Filterable {

    var contactingList = mutableListOf<ContactModel>()
    private var filteredList :List<ContactModel> = contactingList


    fun setContactList(documentlist : List<ContactModel>) {
        this.contactingList.clear()
        this.contactingList.addAll(documentlist)
        filteredList = contactingList
    //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ContactListBinding) : RecyclerView.ViewHolder(binding.root) {
     //   val title: TextView = view.findViewById(R.id.item_title)

        fun bind(item: ContactModel, position: Int) {
            binding.tvName.text = item.name
            binding.tvEmail.text = item.email

            itemView.setOnClickListener { onItemClick(item) }
            binding.btnEdit.setOnClickListener { onEditClick(item,position) }
            binding.btnDelete.setOnClickListener { onDeleteClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ContactListBinding.inflate(inflater,parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredList[position],position)
    }

    override fun getItemCount() = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = contactingList.filter {
                    it.name!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<ContactModel>)
                notifyDataSetChanged()
            }
        }
    }

}
