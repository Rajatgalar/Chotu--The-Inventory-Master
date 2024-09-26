package com.itechnowizard.chotu.presentation.inventory.details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.databinding.ActivityInventoryDetailsBinding
import com.itechnowizard.chotu.domain.model.InventoryModel
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.presentation.inventory.InventoryViewModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InventoryDetails : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryDetailsBinding
    private val viewModel: InventoryViewModel by viewModels()

    private var isForUpdate : Boolean = false
    private var docId : String = ""
    private var listOfProducts : List<InventoryModel>? = null
    private lateinit var adapter: InventoryDetailAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfInventory = mutableListOf<InventoryModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = InventoryDetailAdapter()

        setDataForEdit()

        binding.apply {
            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(toolbarLayout, true, Constants.TOOLBAR_INVENTORY_LIST, Constants.TOOLBAR_NO_MENU_TEXT)
            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when(tab!!.position) {
                        0 -> {setVisibilityOfLayout(true)}
                        1-> {setVisibilityOfLayout(false)}
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })

        }
    }

    private fun setVisibilityOfLayout(isBasic : Boolean){
        if(isBasic){
            binding.stockTimelineLayout.visibility = View.VISIBLE
            binding.detailsLayout.visibility = View.GONE
        }else{
            binding.stockTimelineLayout.visibility = View.GONE
            binding.detailsLayout.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView(list: MutableList<InventoryModel>) {
        adapter.setInventorysList(listOfInventory)
    }


    private fun setDataForEdit() {
        val model = intent.getParcelableExtra<ProductModel>(Constants.INTENT_MODEL)!!

        //docId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        binding.tvSalePrice.text = model.salePrice.toString().ifEmpty { "-" }
        binding.tvStockQty.text = model.totalStock.toString().ifEmpty { "0" }
        binding.tvPurchasePrice.text = model.salePrice.toString().ifEmpty { "-" }
        binding.tvStockValue.text =  (model.purchasePrice.toString().ifEmpty { "0" }.toDouble() * model.totalStock!!).toString()
        binding.tvItemCode.text = "--"
        binding.tvMeasurementUnit.text = model.unit
        binding.tvLowStockAlertAt.text = model.lowStockAlert
        binding.tvGST.text = model.gstPercentType.toString().ifEmpty { "--" }
        binding.tvHSNCode.text = model.hsn.toString().ifEmpty { "--" }

        listOfInventory = model.inventory as MutableList<InventoryModel>
        setupRecyclerView(listOfInventory)

    }

}