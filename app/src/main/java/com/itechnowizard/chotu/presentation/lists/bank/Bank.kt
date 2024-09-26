package com.itechnowizard.chotu.presentation.lists.bank

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.databinding.ActivityBankListBinding
import com.itechnowizard.chotu.domain.model.BankModel
import com.itechnowizard.chotu.presentation.lists.bank.addbank.AddBank
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Bank : AppCompatActivity() {

    private lateinit var binding: ActivityBankListBinding
    private val viewModel: BankViewModel by viewModels()
    private lateinit var adapter: BankAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfBuyer = mutableListOf<BankModel>()
    private var removedPositon: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBankListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BankAdapter(this::onEditClick, this::onDeleteClick,this::onItemClick)

        binding.apply {
            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(toolbarLayout, true, Constants.TOOLBAR_BANK_LIST, Constants.TOOLBAR_ADD_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { startActivity(Intent(this@Bank, AddBank::class.java)) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.loadBankList()

        viewModel.bankListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfBuyer = state.list.toObjects<BankModel>() as MutableList<BankModel>
                setupRecyclerView(listOfBuyer)
            }
        }

        viewModel.removeBankResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Bank details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfBuyer.removeAt(removedPositon)
                    adapter.setBankList(listOfBuyer)

                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting Bank details: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    ProgressBarUtil.showProgressBar(binding.progressBarLayout)
                }
            }
        }
    }

    private fun onEditClick(bankModel: BankModel, position: Int) {

        val intent = Intent(this, AddBank::class.java).apply {
            putExtra(Constants.INTENT_MODEL, bankModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA,true)
        }
        startActivity(intent)

    }

    private fun onDeleteClick(position: Int) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id)
    }

    private fun onItemClick(bankModel: BankModel) {
        val intent = Intent()
        intent.putExtra(Constants.INTENT_MODEL, bankModel)
        intent.putExtra(Constants.INTENT_ACTIVITY,Constants.INTENT_BANK_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showDeleteDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this bank detail?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteBankDocument(documentId)
            }
            .setNegativeButton("Cancel", null)
            .show()
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

    private fun setupRecyclerView(list: List<BankModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setBankList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addBankResult.removeObservers(this)
        viewModel.removeBankResult.removeObservers(this)
    }

}