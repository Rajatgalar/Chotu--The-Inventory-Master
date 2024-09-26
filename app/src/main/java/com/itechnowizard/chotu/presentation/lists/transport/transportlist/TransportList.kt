package com.itechnowizard.chotu.presentation.lists.transport.transportlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.databinding.ActivityBuyerListBinding
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.presentation.lists.buyer.addBuyer.AddBuyer
import com.itechnowizard.chotu.presentation.lists.transport.transportlist.addTransport.AddTransport
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransportList : AppCompatActivity() {
    private lateinit var binding: ActivityBuyerListBinding
    private val viewModel: TransportListViewModel by viewModels()
    private lateinit var adapter: TransportListAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfBuyer = mutableListOf<TransportListModel>()
    private var removedPositon: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuyerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TransportListAdapter(this::onDeleteClick,this::onItemClick)

        binding.apply {
            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(toolbarLayout, true, Constants.TOOLBAR_TRANSPORT_LIST, Constants.TOOLBAR_ADD_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { startActivity(Intent(this@TransportList, AddTransport::class.java)) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }


    override fun onStart() {
        super.onStart()

        viewModel.loadTransportList()

        viewModel.transportListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfBuyer = state.list.toObjects<TransportListModel>() as MutableList<TransportListModel>
                setupRecyclerView(listOfBuyer)
            }
        }

        viewModel.removeTransportResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Transport List Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfBuyer.removeAt(removedPositon)
                    adapter.setBuyersList(listOfBuyer)

                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting Transport list details: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    ProgressBarUtil.showProgressBar(binding.progressBarLayout)
                    // show a progress dialog or something similar
                }
            }
        }
    }

    private fun onEditClick(buyerModel: TransportListModel, position: Int) {

        val intent = Intent(this, AddBuyer::class.java).apply {
            putExtra(Constants.INTENT_MODEL, buyerModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA,true)
        }
        startActivity(intent)
    }

    private fun onDeleteClick(position: Int) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id)
    }

    private fun onItemClick(transport: TransportListModel) {
        val intent = Intent()
        intent.putExtra(Constants.INTENT_MODEL, transport)
//        intent.putExtra(Constants.INTENT_ACTIVITY,Constants.INTENT_TRANSPORT_LIST_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun showDeleteDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this Tranporter?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTransportDocument(documentId)
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

    private fun setupRecyclerView(list: List<TransportListModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setBuyersList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addTransportResult.removeObservers(this)
        viewModel.removeTransportResult.removeObservers(this)
    }


}