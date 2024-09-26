package com.itechnowizard.chotu.presentation.expense

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.tabs.TabLayout
import com.itechnowizard.chotu.databinding.ActivityExpenseBinding
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.presentation.expense.addexpense.AddExpenseActivity
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.SearchViewUtil
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "ExpenseActivity"

@AndroidEntryPoint
class Expense : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private var todayList = emptyList<ExpenseModel>()
    private var thisWeekList = emptyList<ExpenseModel>()
    private var thisMonthList = emptyList<ExpenseModel>()
    private var thisYearList = emptyList<ExpenseModel>()
    private val today = Date()
    private lateinit var adapter : AllExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = AllExpenseAdapter()

        binding.apply {

            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(toolbarLayout,true,Constants.TOOLBAR_EXPENSES,Constants.TOOLBAR_CREATE_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { openAddExpenseActivity() }
            floatingButton.setOnClickListener { openAddExpenseActivity() }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                @Override
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    Log.d(TAG, "onTabSelected: ${tab!!.position}")
                    when(tab.position) {
                        0 -> {setDataOnRecyclerView(todayList)}
                        1-> {setDataOnRecyclerView(thisWeekList)}
                        2-> setDataOnRecyclerView(thisMonthList)
                        3-> setDataOnRecyclerView(thisYearList)
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })

            toolbarLayout.toolbarMenuText.setOnClickListener {
                startActivity(Intent(this@Expense, AddExpenseActivity::class.java))
            }

            toolbarLayout.toolbarBack.setOnClickListener {
                finish()
            }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }

    }

    private fun openAddExpenseActivity(){
        startActivity(Intent(this@Expense, AddExpenseActivity::class.java))
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadAllExpense()
        viewModel.expenseList.observe(this) { expenses ->
            if (expenses.isNullOrEmpty())
                setDataOnRecyclerView(todayList)
            else
                filterfun(expenses)
        }
    }

    private fun filterfun(expenses: List<ExpenseModel>?) {

        val formatterOut = SimpleDateFormat("dd-MM-yyyy")
        lateinit var date: Date
        todayList = expenses!!.filter {
            date = formatterOut.parse(it.dateOfExpense)!!
            date.isToday(today)
        }
        thisWeekList = expenses.filter {
            date = formatterOut.parse(it.dateOfExpense)!!
            date.isThisWeek(today)
        }
        thisMonthList = expenses.filter {
            date = formatterOut.parse(it.dateOfExpense)!!
            date.isThisMonth(today)
        }
        thisYearList = expenses.filter {
            date = formatterOut.parse(it.dateOfExpense)!!
            date.isThisYear(today)
        }
        setDataOnRecyclerView(todayList)
    }

    private fun calculateTotalExpense(listOfExpense: List<ExpenseModel>){
        var amount = 0
        listOfExpense.forEach {
            amount += it.amount!!.toInt()
        }
        binding.tvAmount.text = amount.toString()
    }

    private fun setDataOnRecyclerView(listOfExpense: List<ExpenseModel>) {
        if(listOfExpense.isEmpty()){
            setVisibilityOfRecyclerView(false)
            binding.tvAmount.text="00"
        }else{
            setVisibilityOfRecyclerView(true)
          //  binding.recyclerView.adapter = AllExpenseAdapter(listOfExpense)
            adapter.setExpensesList(listOfExpense)
            calculateTotalExpense(listOfExpense)
        }
    }

    private fun setVisibilityOfRecyclerView(recyclerview: Boolean) {
        if(recyclerview){
            binding.tvNoData.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.VISIBLE
        }else{
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        }
    }

    // extension functions to check if date is today, this week, this month or this year
    fun Date.isToday(today: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = this@isToday }
        val cal2 = Calendar.getInstance().apply { time = today }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun Date.isThisWeek(today: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = this@isThisWeek }
        val cal2 = Calendar.getInstance().apply { time = today }
        val diff = cal1.get(Calendar.WEEK_OF_YEAR) - cal2.get(Calendar.WEEK_OF_YEAR)
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                (diff == 0 || (diff == -1 && cal1.get(Calendar.DAY_OF_WEEK) > cal2.get(Calendar.DAY_OF_WEEK)))
    }

    fun Date.isThisMonth(today: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = this@isThisMonth }
        val cal2 = Calendar.getInstance().apply { time = today }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
    }

    fun Date.isThisYear(today: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = this@isThisYear }
        val cal2 = Calendar.getInstance().apply { time = today }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
    }
}