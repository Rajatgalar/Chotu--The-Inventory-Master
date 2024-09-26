package com.itechnowizard.chotu.presentation.lists.seller

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
import com.itechnowizard.chotu.databinding.ActivitySellerBinding
import com.itechnowizard.chotu.domain.model.SellerModel
import com.itechnowizard.chotu.presentation.lists.seller.addSeller.AddSeller
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Seller : AppCompatActivity() {
    private lateinit var binding: ActivitySellerBinding
    private val viewModel: SellerViewModel by viewModels()
    private lateinit var adapter: SellerAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfSeller = mutableListOf<SellerModel>()
    private var removedPositon: Int = -1
    private var returnResult : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        returnResult = intent.getBooleanExtra(Constants.RETURN_RESULT, true)
        adapter = SellerAdapter(this::onEditClick, this::onDeleteClick,this::onItemClick)

        binding.apply {
            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(toolbarLayout, true, Constants.TOOLBAR_SELLER_LIST, Constants.TOOLBAR_ADD_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { startActivity(Intent(this@Seller, AddSeller::class.java)) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }


    override fun onStart() {
        super.onStart()

        viewModel.loadSellerList()

        viewModel.sellerListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfSeller = state.list.toObjects<SellerModel>() as MutableList<SellerModel>
                setupRecyclerView(listOfSeller)
            }
        }

        viewModel.removeSellerResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Seller details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfSeller.removeAt(removedPositon)
                    adapter.setSellersList(listOfSeller)

                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting Seller details: ${result.message}",
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

    private fun onEditClick(sellerModel: SellerModel, position: Int) {

        val intent = Intent(this, AddSeller::class.java).apply {
            putExtra(Constants.INTENT_MODEL, sellerModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA,true)
        }
        startActivity(intent)
    }

    private fun onDeleteClick(position: Int) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id)
    }

    private fun onItemClick(sellerModel: SellerModel,position: Int) {
        if (returnResult) {
            val intent = Intent()
            intent.putExtra(Constants.INTENT_MODEL, sellerModel)
            intent.putExtra(Constants.INTENT_ACTIVITY, Constants.INTENT_SELLER_ACTIVITY)
            intent.putExtra(Constants.INTENT_SELLER_ID,documentSnapshot[position].id)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun showDeleteDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this seller?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteSellerDocument(documentId)
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

    private fun setupRecyclerView(list: List<SellerModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setSellersList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addSellerResult.removeObservers(this)
        viewModel.removeSellerResult.removeObservers(this)
    }


}