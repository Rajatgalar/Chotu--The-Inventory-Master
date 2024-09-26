package com.itechnowizard.chotu.presentation.ledger.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.itechnowizard.chotu.databinding.ActivityDetailLedgerBinding
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.presentation.ledger.LedgerViewModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailLedger : AppCompatActivity() {

    private lateinit var binding: ActivityDetailLedgerBinding
    private lateinit var adapter: DetailLedgerAdapter
    private val viewModel: LedgerViewModel by viewModels()
    private lateinit var ledgerList: List<BuyerSellerLedgerModel>
    var isBuyer = false
    var name = "Ledger"
    var documentId = "" ///doc id of buyer/seller


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLedgerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isBuyer = intent.getBooleanExtra(Constants.IS_BUYER,false)
        name = intent.getStringExtra(Constants.FIREBASE_NAME).toString()
        documentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()


        adapter = DetailLedgerAdapter(this::onItemClick)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            recyclerView.adapter = adapter

            ToolbarUtils.setToolbar(toolbarLayout,true, name, Constants.TOOLBAR_NO_MENU_TEXT)

            toolbarLayout.toolbarBack.setOnClickListener { finish() }
        }

    }

    override fun onStart() {
        super.onStart()

        viewModel.loadDetailLedger(buyerId = documentId,isBuyer)

        viewModel.detailLedgerState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                ledgerList = state.list
                setupRecyclerView(ledgerList)
            }
        }

    }

    private fun onItemClick( position: Int) {

//        val intent = Intent(this, AddInvoiceActivity::class.java).apply {
//            putExtra(Constants.INTENT_MODEL, invoiceModel)
//            putExtra(Constants.DOCUMENT_ID, )
//            putExtra(Constants.IS_INTENT_HAVING_DATA,true)
//        }
//        startActivity(intent)
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
            adapter.setLedgerList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.detailLedgerState.removeObservers(this)
    }


}