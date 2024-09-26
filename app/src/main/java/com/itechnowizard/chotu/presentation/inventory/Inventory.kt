package com.itechnowizard.chotu.presentation.inventory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.databinding.ActivityInventoryBinding
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.presentation.inventory.details.InventoryDetails
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Inventory : AppCompatActivity() {
    private lateinit var binding : ActivityInventoryBinding
    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var adapter: InventoryAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfInventory = mutableListOf<ProductModel>()
    private var removedPositon: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = InventoryAdapter(this::onEditClick, this::onDeleteClick,this::onItemClick)

        binding.apply {
            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(toolbarLayout, true, Constants.TOOLBAR_INVENTORY_LIST, Constants.TOOLBAR_NO_MENU_TEXT)

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }


    override fun onStart() {
        super.onStart()

        viewModel.loadInventoryProduct()

        viewModel.inventoryListState.observe(this){ state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.data != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                listOfInventory = state.data as MutableList<ProductModel>
                setupRecyclerView(listOfInventory)
            }
        }

    }

    private fun onEditClick(inverntoryModel: ProductModel, position: Int) {

        val intent = Intent(this, InventoryDetails::class.java).apply {
            putExtra(Constants.INTENT_MODEL, inverntoryModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA,true)
        }
        startActivity(intent)
    }

    private fun onDeleteClick(position: Int) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id)
    }

    private fun onItemClick(inverntoryModel: ProductModel,position: Int) {
        val intent = Intent(this, InventoryDetails::class.java).apply {
            putExtra(Constants.INTENT_MODEL, inverntoryModel)
        }
        startActivity(intent)
    }

    private fun showDeleteDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this inverntory?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteInventoryDocument(documentId)
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

    private fun setupRecyclerView(list: List<ProductModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            calculateTotalStock(list)

        }
    }

    private fun calculateTotalStock(productList: List<ProductModel>) {
        for (product in productList) {
            var totalStock = 0

            // loop through each inventory of the product if its not null
            if(!product.inventory.isNullOrEmpty()) {
                for (inventory in product.inventory!!) {
                    totalStock += inventory.stock ?: 0
                }
            }
            product.totalStock = totalStock
        }

        adapter.setInventorysList(productList)
    }

    override fun onStop() {
        super.onStop()
        viewModel.addInventoryResult.removeObservers(this)
        viewModel.removeInventoryResult.removeObservers(this)
    }

}