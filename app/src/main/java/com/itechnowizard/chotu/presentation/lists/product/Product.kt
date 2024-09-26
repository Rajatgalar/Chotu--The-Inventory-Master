package com.itechnowizard.chotu.presentation.lists.product

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityProductListBinding
import com.itechnowizard.chotu.databinding.DialogAddSelectedProductBinding
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.model.SelectedProductModel
import com.itechnowizard.chotu.presentation.lists.product.addProduct.AddProduct
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Product : AppCompatActivity() {

    private lateinit var binding: ActivityProductListBinding
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var adapter: ProductAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfProduct = mutableListOf<ProductModel>()
    private var removedPositon: Int = -1
    private var gstValue = 0.0
    private var cessValue = 0.0
    private var discountValue = 0.0
    private var taxableAmount = 00.00
    private var Amount = 00.00
    private var finalTotalAmount = 00.00
    private var returnResult : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        returnResult = intent.getBooleanExtra(Constants.RETURN_RESULT, true)
        adapter = ProductAdapter(this::onEditClick, this::onDeleteClick, this::onItemClick)

        binding.apply {

            recyclerView.adapter = adapter
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(
                toolbarLayout,
                true,
                Constants.TOOLBAR_PRODUCT_LIST,
                Constants.TOOLBAR_ADD_NEW
            )

            toolbarLayout.toolbarMenuText.setOnClickListener {
                startActivity(Intent(this@Product, AddProduct::class.java))
            }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.loadProductList()

        viewModel.productListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfProduct = state.list.toObjects<ProductModel>() as MutableList<ProductModel>
                setupRecyclerView(listOfProduct)
            }
        }

        viewModel.removeProductResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Product details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfProduct.removeAt(removedPositon)
                    adapter.setProductsList(listOfProduct)

                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting Product details: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    // show a progress dialog or something similar
                    ProgressBarUtil.showProgressBar(binding.progressBarLayout)
                }
            }

        }
    }

    private fun onEditClick(productModel: ProductModel, position: Int) {

        val intent = Intent(this, AddProduct::class.java).apply {
            putExtra(Constants.INTENT_MODEL, productModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA, true)
        }
        startActivity(intent)
    }

    private fun onDeleteClick(position: Int) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id)
    }

    private fun onItemClick(productModel: ProductModel, position: Int) {

        if (returnResult) {
            val dialogBinding = DialogAddSelectedProductBinding.inflate(layoutInflater)
            val dialog = Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(dialogBinding.root)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )


            dialogBinding.apply {

                etUnit.setAdapter(
                    ArrayAdapter(
                        this@Product,
                        R.layout.list_item,
                        R.id.item_title,
                        resources.getStringArray(R.array.ProductUnit)
                    )
                )
                DiscountPercentSpinner.setAdapter(
                    ArrayAdapter(
                        this@Product,
                        R.layout.custom_spinner_item,
                        resources.getStringArray(R.array.DiscountPercent)
                    )
                )

                etGSTSpinner.setAdapter(
                    ArrayAdapter(
                        this@Product,
                        android.R.layout.simple_spinner_dropdown_item,
                        resources.getStringArray(R.array.GstPercent)
                    )
                )

                etCESSPercentSpinner.setAdapter(
                    ArrayAdapter(
                        this@Product,
                        android.R.layout.simple_spinner_item,
                        resources.getStringArray(R.array.CessPercent)
                    )
                )

                ToolbarUtils.setToolbar(
                    toolbarLayout,
                    false,
                    Constants.TOOLBAR_ADD_PRODUCT,
                    Constants.TOOLBAR_NO_MENU_TEXT
                )
                etItemName.setText(productModel.itemName)
                etItemName.isEnabled = false
                etQuantity.setText("0")
                etHSN.setText(productModel.hsn)
                etUnitPrice.setText(productModel.salePrice)
                tvTotalAmount.text = "0"
                etDiscount.setText("0")
                tvTotalTaxableAmount.text = "0"
                switchTaxInclusiveExclusive.isChecked =
                    !productModel.taxInclusiveExclusiveType.equals("1")
                etGSTSpinner.setText(productModel.gstPercentType)
                etCESS.setText(productModel.cess)
                etCESSPercentSpinner.setText(productModel.cessType)
                tvfinalTotalAmount.text = "0"

                val textWatcherForAmount = object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        val value = s.toString().toFloatOrNull()
                        if (value != null) {
                            val quantity = etQuantity.text.toString().toDoubleOrNull() ?: 0.0
                            val price = etUnitPrice.text.toString().toDoubleOrNull() ?: 0.0
                            Amount = quantity * price
                            tvTotalAmount.text = String.format("%.2f", Amount)
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                }

                val textWatcherForDiscountAmount = object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        updateDiscountTextView(
                            tvTotalAmount.text.toString(), DiscountPercentSpinner.text.toString(),
                            s.toString(), tvTotalTaxableAmount
                        )
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                }

                val textWatcherForTvAmount = object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        updateDiscountTextView(
                            s.toString(), DiscountPercentSpinner.text.toString(),
                            etDiscount.text.toString(), tvTotalTaxableAmount
                        )
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                }

                val textWatcherForTotalAmount = object : TextWatcher {
                    override fun afterTextChanged(s: Editable) {
                        val value = s.toString().toFloatOrNull()
                        if (value != null) {
                            if (switchTaxInclusiveExclusive.isChecked) {
                                updateFinalAmountTextview(
                                    etGSTSpinner.text.toString(), etCESS.text.toString(),
                                    etCESSPercentSpinner.text.toString(), tvfinalTotalAmount
                                )
                            } else {
                                tvfinalTotalAmount.text = tvTotalTaxableAmount.text
                            }
                        }
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                }

                etQuantity.addTextChangedListener(textWatcherForAmount)
                etUnitPrice.addTextChangedListener(textWatcherForAmount)
                tvTotalAmount.addTextChangedListener(textWatcherForTvAmount)

                etCESS.addTextChangedListener(textWatcherForTotalAmount)
                tvTotalTaxableAmount.addTextChangedListener(textWatcherForTotalAmount)

                switchTaxInclusiveExclusive.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        updateFinalAmountTextview(
                            etGSTSpinner.text.toString(), etCESS.text.toString(),
                            etCESSPercentSpinner.text.toString(), tvfinalTotalAmount
                        )
                    } else tvfinalTotalAmount.text = tvTotalTaxableAmount.text
                }

                etDiscount.addTextChangedListener(textWatcherForDiscountAmount)
                DiscountPercentSpinner.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, _, _ ->
                        updateDiscountTextView(
                            tvTotalAmount.text.toString(), DiscountPercentSpinner.text.toString(),
                            etDiscount.text.toString(), tvTotalTaxableAmount
                        )
                    }
                etGSTSpinner.onItemClickListener = AdapterView.OnItemClickListener { _, _, _, _ ->
                    updateDiscountTextView(
                        tvTotalAmount.text.toString(), DiscountPercentSpinner.text.toString(),
                        etDiscount.text.toString(), tvTotalTaxableAmount
                    )
                }

                etCESSPercentSpinner.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, _, _ ->
                        updateFinalAmountTextview(
                            etGSTSpinner.text.toString(), etCESS.text.toString(),
                            etCESSPercentSpinner.text.toString(), tvfinalTotalAmount
                        )
                    }
            }

            dialogBinding.btnSave.setOnClickListener {
                val intent = Intent()
                println("final total amount in product : $finalTotalAmount")
                intent.putExtra(
                    Constants.INTENT_MODEL, SelectedProductModel(
                        amount = Amount,
                        cess = dialogBinding.etCESS.text.toString().ifBlank { "0" },
                        cessType = dialogBinding.etCESSPercentSpinner.text.toString(),
                        cessValue = cessValue,
                        discount = dialogBinding.etDiscount.text.toString().ifBlank { "0" },
                        discountType = dialogBinding.DiscountPercentSpinner.text.toString(),
                        discountValue = discountValue,
                        gstPercentType = dialogBinding.etGSTSpinner.text.toString(),
                        gstValue = gstValue,
                        hsn = dialogBinding.etHSN.text.toString().ifBlank { "0" },
                        itemId = documentSnapshot[position].id,
                        itemName = dialogBinding.etItemName.text.toString(),
                        quantity = dialogBinding.etQuantity.text.toString().ifBlank { "0" },
                        unitPrice = dialogBinding.etUnitPrice.text.toString().ifBlank { "0" },
                        taxInclusiveExclusiveType = if (dialogBinding.switchTaxInclusiveExclusive.isChecked) "2" else "1",
                        taxableAmount = taxableAmount,
                        totalAmount = dialogBinding.tvfinalTotalAmount.text.toString().toDouble(),
                        unitType = dialogBinding.etUnit.text.toString()
                    )
                )
                intent.putExtra(Constants.INTENT_ACTIVITY, Constants.INTENT_PRODUCT_ACTIVITY)
                setResult(Activity.RESULT_OK, intent)
                dialog.dismiss()
                finish()
            }
//            dialogBinding.DiscountPercentSpinner.setText(resources.getStringArray(R.array.DiscountPercent)[0])
//            dialogBinding.etUnit.setText(resources.getStringArray(R.array.ProductUnit)[0])
//            dialogBinding.etGSTSpinner.setText(resources.getStringArray(R.array.GstPercent)[0])
//            dialogBinding.etCESSPercentSpinner.setText(resources.getStringArray(R.array.CessPercent)[0])

            dialog.show()
        }
    }

    private fun updateFinalAmountTextview(gstSpinner: String, cess: String, cessType: String, tvfinalTotalAmount: TextView) {

        val cess = cess.toDoubleOrNull() ?:0.0
        val regex = "(\\d+(\\.\\d+)?%)".toRegex()
        val matchResult = regex.find(gstSpinner)
        if (matchResult != null) {
            val matchText = matchResult.value.replace("%", "")
            gstValue = matchText.toDouble()
        }

        when (cessType) {
            "(Unit Wise)" -> {
                cessValue = cess
            }
            "(% Percent Wise)" -> {
                cessValue =  (taxableAmount*cess/100)
            }
        }
        gstValue = (taxableAmount*gstValue/100)
        println("gstValue : $gstValue")

        finalTotalAmount = taxableAmount + gstValue + cessValue
        tvfinalTotalAmount.text = "$finalTotalAmount"

    }

    private fun updateDiscountTextView(amount : String,selectedType: String, discountInput: String, discountValueTextView: TextView) {
        val discount = discountInput.toDoubleOrNull() ?: 0.0
        val amount = amount.toDoubleOrNull() ?: 0.0
        when (selectedType) {
            "(₹ Amount Wise)" -> { discountValue = discount }
            "(% Percent Wise)" -> { discountValue = ((amount *discount / 100)) }
        }
        taxableAmount = amount - discountValue
        discountValueTextView.text = "$taxableAmount"
    }

    private fun showDeleteDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteProductDocument(documentId)
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
            adapter.setProductsList(list)
        }
    }
}