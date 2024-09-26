package com.itechnowizard.chotu.presentation.payment.receipt.addpayment.invoicelist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.databinding.ActivityInvoiceListBinding
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvoiceList : AppCompatActivity() {
    private lateinit var binding : ActivityInvoiceListBinding
    private val viewModel: InvoiceListViewModel by viewModels()
    private lateinit var adapter: InvoiceListAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfBuyer = mutableListOf<BuyerSellerLedgerModel>()
    private var buyerId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buyerId = intent.getStringExtra("buyerId").toString()

        adapter = InvoiceListAdapter(this::onItemClick)

        binding.apply {
            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(toolbarLayout, true, Constants.TOOLBAR_INVOICE_LIST, Constants.TOOLBAR_NO_MENU_TEXT)

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.loadTransportList(buyerId)

        viewModel.invoiceListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfBuyer = state.list.toObjects<BuyerSellerLedgerModel>() as MutableList<BuyerSellerLedgerModel>
                setupRecyclerView(listOfBuyer)
            }
        }
    }


    private fun onItemClick(transport: BuyerSellerLedgerModel, position : Int) {
        val intent = Intent()
        intent.putExtra(Constants.INTENT_MODEL, transport)
        intent.putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
        intent.putExtra(Constants.INTENT_ACTIVITY,Constants.INTENT_INVOICE_LIST_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
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

    private fun setupRecyclerView(list: List<BuyerSellerLedgerModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setBuyersList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.invoiceListState.removeObservers(this)
    }

}