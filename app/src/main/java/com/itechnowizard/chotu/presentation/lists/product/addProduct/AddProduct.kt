package com.itechnowizard.chotu.presentation.lists.product.addProduct

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityAddProductBinding
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.presentation.lists.product.ProductViewModel
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProduct : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: ProductViewModel by viewModels()
    private var isForUpdate : Boolean = false
    private var DocumentId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isForUpdate = intent.getBooleanExtra(Constants.IS_INTENT_HAVING_DATA,false)
        if(isForUpdate){
            setDataForEdit()
        }else
            setDataForAdd()

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)

            setAdapters()

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

            etExpiryDate.setOnClickListener {
                DateUtils.showDatePicker(this@AddProduct) { selectedDate ->
                    binding.etExpiryDate.setText(selectedDate)
                }
            }

            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }
            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            btnSave.setOnClickListener { saveData() }


        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.addProductResult.observe(this@AddProduct){ result->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this@AddProduct, "Product details Saved successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this@AddProduct,
                        "Error adding Product details: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    // show a progress dialog or something simi1lar
                    ProgressBarUtil.showProgressBar(binding.progressBarLayout)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addProductResult.removeObservers(this)
    }

    private fun setAdapters() {
        binding.apply {

            etUnit.setAdapter(
                ArrayAdapter(this@AddProduct, R.layout.list_item,R.id.item_title,
                    resources.getStringArray(R.array.ProductUnit))
            )

            etGSTSpinner.setAdapter(ArrayAdapter(this@AddProduct,
                android.R.layout.simple_spinner_item,resources.getStringArray(R.array.GstPercent)
            ))

            etCESSPercentSpinner.setAdapter(ArrayAdapter(this@AddProduct,
                android.R.layout.simple_spinner_item,resources.getStringArray(R.array.CessPercent)
            ))

            etExclusiveGSTSpinner.setAdapter(ArrayAdapter(this@AddProduct,
                android.R.layout.simple_spinner_item,resources.getStringArray(R.array.ExclusiveInclusiveGst)
            ))
        }
    }

    private fun setDataForAdd() {
        ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_ADD_PRODUCT,Constants.TOOLBAR_SAVE)
    }

    private fun setDataForEdit() {

        val model = intent.getParcelableExtra<ProductModel>(Constants.INTENT_MODEL)!!
        DocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        binding.apply {
            etItemName.setText(model.itemName)

            etHSN.setText(model.hsn)
            etUnitPrice.setText(model.salePrice)
            etUnit.setText(model.unit)
            switchTaxInclusiveExclusive.isChecked = !model.taxInclusiveExclusiveType.equals("1")
            etGSTSpinner.setText(model.gstPercentType)
            etCESS.setText(model.cess)
            etCESSPercentSpinner.setText(model.cessType)
            etPurchasePrice.setText(model.purchasePrice)
            etExclusiveGSTSpinner.setText(model.purchaseType)
            switchMaintainStock.isChecked = !model.mainStock.equals("1")
            etOpeningStock.setText(model.openingStock)
            etOpeningStockDate.setText(model.openingStockDate)
            etLowStockAlert.setText(model.lowStockAlert)
        }

        ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_EDIT_PRODUCT,Constants.TOOLBAR_UPDATE)
        binding.btnSave.text = Constants.TOOLBAR_UPDATE

    }


    private fun saveData() {
        val message = checkValidation()
        if (message == Constants.OK) {
            binding.apply {

                viewModel.addProduct(ProductModel(
                    itemName = binding.etItemName.text.toString(),
                    cess = binding.etCESS.text.toString(),
                    cessType = binding.etCESSPercentSpinner.text.toString(),
                    expiryDate=binding.etExpiryDate.text.toString().ifBlank { "" },
                    hsn = binding.etHSN.text.toString(),
                    salePrice = binding.etUnitPrice.text.toString().ifBlank { "0" },
                    unit = binding.etUnit.text.toString(),
                    taxInclusiveExclusiveType =if (binding.switchTaxInclusiveExclusive.isChecked) "2" else "1",
                    gstPercentType = binding.etGSTSpinner.text.toString(),
                    purchasePrice = binding.etPurchasePrice.text.toString(),
                    purchaseType =binding.etExclusiveGSTSpinner.text.toString(),
                    mainStock = if (binding.switchMaintainStock.isChecked) "2" else "1",
                    openingStock = binding.etOpeningStock.text.toString(),
                    openingStockDate = binding.etOpeningStockDate.text.toString(),
                    lowStockAlert = binding.etLowStockAlert.text.toString(),
                ),isForUpdate,DocumentId)
            }
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkValidation(): String {
        if (binding.etItemName.text.isNullOrEmpty())
            return "Enter Item Name"
        return Constants.OK
    }

    private fun setVisibilityOfLayout(isBasic : Boolean){
        if(isBasic){
            binding.basicDetailsLayout.visibility = View.VISIBLE
            binding.constraintOptional.visibility = View.INVISIBLE
        }else{
            binding.basicDetailsLayout.visibility = View.INVISIBLE
            binding.constraintOptional.visibility = View.VISIBLE
        }
    }
}
