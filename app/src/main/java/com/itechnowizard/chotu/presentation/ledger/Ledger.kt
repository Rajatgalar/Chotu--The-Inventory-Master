package com.itechnowizard.chotu.presentation.ledger

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.itechnowizard.chotu.databinding.ActivityLedgerBinding
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.presentation.ledger.detail.DetailLedger
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Ledger : AppCompatActivity() {

    private lateinit var binding: ActivityLedgerBinding
    private lateinit var adapter: LedgerAdapter
    private val viewModel: LedgerViewModel by viewModels()
    private lateinit var creditsList: List<CreditorDebitorModel>
    private lateinit var debitsList: List<CreditorDebitorModel>
    private var isBuyer = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLedgerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = LedgerAdapter(this::onItemClick)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            recyclerView.adapter = adapter
            ToolbarUtils.setToolbar(toolbarLayout,true, Constants.FIREBASE_LEDGER, Constants.TOOLBAR_NO_MENU_TEXT)

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                @Override
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab!!.position) {
                        0 -> {
                            isBuyer = false
                            setupRecyclerView(creditsList)
                        }
                        1-> {
                            isBuyer = true
                            setupRecyclerView(debitsList)
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })

        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.loadLedger()

        viewModel.ledgerState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.data != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)

                debitsList = if (state.data.debitorList.isNullOrEmpty())
                    emptyList()
                else
                    state.data.debitorList!!

                creditsList = if (state.data.creditorList.isNullOrEmpty())
                    emptyList()
                else
                    state.data.creditorList!!

                if(isBuyer)
                    setupRecyclerView(debitsList)
                else
                    setupRecyclerView(creditsList)
            }
        }
    }


    private fun onItemClick(invoiceModel: CreditorDebitorModel, position: Int) {

        val intent = Intent(this, DetailLedger::class.java).apply {
            putExtra(Constants.DOCUMENT_ID, invoiceModel.id)
            putExtra(Constants.FIREBASE_NAME, invoiceModel.name)
            putExtra(Constants.IS_BUYER,isBuyer)
        }
        startActivity(intent)
    }


    private fun setVisiblityOfRecyclerView(recyclerview: Boolean) {
        if (recyclerview) {
            binding.tvNoData.visibility = View.INVISIBLE
            binding.recyclerView.visibility = View.VISIBLE
        } else {
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.INVISIBLE
        }
    }

    private fun setupRecyclerView(list: List<CreditorDebitorModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setLedgerList(list,isBuyer)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.ledgerState.removeObservers(this)
    }


}
