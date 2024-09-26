package com.itechnowizard.chotu.presentation.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ExpenseAllexpenseBinding
import com.itechnowizard.chotu.domain.model.ExpenseModel

class AllExpenseAdapter() : RecyclerView.Adapter<AllExpenseAdapter.ViewHolder>(), Filterable{

    var expenseList= mutableListOf<ExpenseModel>()

    private var filteredList :List<ExpenseModel> = expenseList

    fun setExpensesList(listOfExpense: List<ExpenseModel>) {
        this.expenseList.clear()
        this.expenseList.addAll(listOfExpense)
        filteredList = expenseList
        //filteredList.addAll(this.expenseList)
        //    this.buyerList = documentlist.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ExpenseAllexpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ExpenseAllexpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(filteredList[position]){
                binding.tvSpendFor.text = this.spendFor
                binding.tvAmount.text = this.amount
                binding.tvDate.text = this.dateOfExpense
                binding.tvCategoryType.text = this.category
                when(this.category){
                    "Bill and Utilities" -> binding.ivIcon.setImageResource(R.drawable.ic_tax)
                    "Food and Dining" -> binding.ivIcon.setImageResource(R.drawable.ic_food)
                    "Gifts and Decorations" -> binding.ivIcon.setImageResource(R.drawable.ic_gifts)
                    "Health and Fitness" -> binding.ivIcon.setImageResource(R.drawable.ic_health)
                    "Investments" -> binding.ivIcon.setImageResource(R.drawable.ic_investments)
                    "Travel" -> binding.ivIcon.setImageResource(R.drawable.ic_travel)
                    "Other" -> binding.ivIcon.setImageResource(R.drawable.ic_other)
                    "Personal Care" -> binding.ivIcon.setImageResource(R.drawable.ic_personal_care)
                    "Recharge" -> binding.ivIcon.setImageResource(R.drawable.ic_rechare)
                    "Taxes" -> binding.ivIcon.setImageResource(R.drawable.ic_tax)
                    "Shopping" -> binding.ivIcon.setImageResource(R.drawable.ic_shopping)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredResults = expenseList.filter {
                    it.spendFor!!.contains(constraint.toString(), ignoreCase = true)
                }
                return FilterResults().apply {
                    values = filteredResults
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = (results?.values as List<ExpenseModel>)
                notifyDataSetChanged()
            }
        }
    }


}