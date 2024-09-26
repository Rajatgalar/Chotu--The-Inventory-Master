package com.itechnowizard.chotu.presentation.creditnote.addcredit

import android.R
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.github.gcacace.signaturepad.views.SignaturePad
import com.itechnowizard.chotu.databinding.ActivityAddCreditBinding
import com.itechnowizard.chotu.databinding.ActivityAddDebitBinding
import com.itechnowizard.chotu.databinding.SignatureDialogBinding
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.presentation.creditnote.CreditNoteViewModel
import com.itechnowizard.chotu.presentation.debitnote.DebitNoteViewModel
import com.itechnowizard.chotu.presentation.lists.consignee.Consignee
import com.itechnowizard.chotu.presentation.lists.product.Product
import com.itechnowizard.chotu.presentation.lists.buyer.Buyer
import com.itechnowizard.chotu.presentation.lists.supplier.Supplier
import com.itechnowizard.chotu.presentation.lists.transport.TransportDetails
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_debit.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL

@AndroidEntryPoint
class AddCredit : AppCompatActivity() {

    private lateinit var binding: ActivityAddCreditBinding
    private val viewModel: CreditNoteViewModel by viewModels()
    private var isForUpdate: Boolean = false
    private var docId: String = ""
    private var supplierModel = SupplierModel()
    private var buyerModel = BuyerModel()
    private var transportModel = TransportModel()
    private var selectedProductModel = SelectedProductModel()
    private var consigneeModel = ConsigneeModel()
    private var listOfProducts = mutableListOf<SelectedProductModel>()
    private lateinit var productAdapter: CreditNoteProductAdapter
    private var signatureBitmap: Bitmap? = null
    private var bitmapByteData: ByteArray? = null
    private var creditNoteCode = ""
    var totalGST = 0.0
    var totalCESS = 0.0
    var totalAmount = 0.0
    var totalTaxableAmount = 0.0
    var totalTotalTax = 0.0
    var totalTotalAmount = 0.0
    private var previousBillFinalAmount = 0.0
    private var buyerId = ""
    private var freightCharge =0.0
    private var InsuranceCharge =0.0
    private var LoadingCharge =0.0
    private var PackagingCharge =0.0
    private var OtherCharge =0.0
    private var TCSCharge =0.0
    private var billFinalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCreditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isForUpdate = intent.getBooleanExtra(Constants.IS_INTENT_HAVING_DATA, false)

        productAdapter =
            CreditNoteProductAdapter(this::onDeleteClick, this::onItemClick)

        if (isForUpdate) {
            setDataForEdit()
        } else
            setDataForAdd()

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            productRecyclerView.adapter = productAdapter

            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }
            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            btnSign.setOnClickListener { openDialog() }

            btnAddSupplierDetails.setOnClickListener { startNewActivityForResult(Supplier::class.java) }
            btnBuyerDetails.setOnClickListener { startNewActivityForResult(Buyer::class.java) }
            btnConsigneeDetails.setOnClickListener { startNewActivityForResult(Consignee::class.java) }
            btnProductDetails.setOnClickListener { startNewActivityForResult(Product::class.java) }
            btnTransportDetails.setOnClickListener {
                openActivityWithData(
                    transportModel,
                    TransportDetails::class.java
                )
            }

            ivEditSupplier.setOnClickListener { startNewActivityForResult(Supplier::class.java) }
            ivEditBuyer.setOnClickListener { startNewActivityForResult(Buyer::class.java) }
            ivEditTransport.setOnClickListener {
                openActivityWithData(
                    transportModel,
                    TransportDetails::class.java
                )
            }

            radioGroup2.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    r2b1.id -> {
                        setConsigneeVisibility(false)
                    }
                    r2b2.id -> {
                        setConsigneeVisibility(false)
                    }
                    r2b3.id -> {
                        setConsigneeVisibility(true)
                    }
                }
            }
        }


    }

    private fun <T : Parcelable> openActivityWithData(model: T, activityClass: Class<*>) {
        val intent = Intent(this, activityClass).putExtra(Constants.INTENT_DATA, model)
        startForResult.launch(intent)
    }

    private fun startNewActivityForResult(destination: Class<*>) {
        startForResult.launch(Intent(this, destination))
    }

    private fun setDataForAdd() {
        binding.etCreditNoteDate.setText(DateUtils.getTodayDate())
        ToolbarUtils.setToolbar(
            binding.toolbarLayout,
            true,
            Constants.CREDITNOTE,
            Constants.TOOLBAR_SAVE
        )
    }

    private fun setDataForEdit() {

        val model = intent.getParcelableExtra<CreditNoteModel>(Constants.INTENT_MODEL)!!
        docId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        supplierModel = model.supplierDetail!!
        buyerModel = model.buyerDetail!!
        listOfProducts.addAll(model.productDetails!!)
        transportModel = model.transportDetails!!

        previousBillFinalAmount = model.billFinalAmount!!
        buyerId = model.buyerId!!

        if(model.creditNoteCode!!.isNotEmpty()) creditNoteCode = model.creditNoteCode!!

        binding.apply {

            binding.etCreditNoteNumber.setText(model.creditNoteNumber)
            binding.etCreditNoteDate.setText(model.creditNoteDate)
            when (model.consigneeDetailType) {
                "Show Consignee (Same as above)" -> r2b1.isChecked = true
                "Consignee Not Required" -> r2b2.isChecked = true
                "Add Consignee (If different from above)" -> {
                    r2b3.isChecked = true
                    consigneeModel = model.consigneeModel!!
                    setConsigneeVisibility(true)
                }
            }

            binding.etTermsAndConditions.setText(model.termsAndCondition)

            if (!model.imageUrl.isNullOrEmpty())
                loadImageFromUrl(model.imageUrl!!)
            ToolbarUtils.setToolbar(
                toolbarLayout,
                true,
                Constants.TOOLBAR_Edit_CREDITNOTE,
                Constants.TOOLBAR_UPDATE
            )
        }

        setSupplierData()
        setBuyerData()
        setProductData()
        setTransportData()
    }

    private fun loadImageFromUrl(url: String) {
        lifecycleScope.launch {
            // switch to IO dispatcher to perform network operation
            signatureBitmap = withContext(Dispatchers.IO) {
                try {
                    val inputStream = URL(url).openConnection().getInputStream()
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: IOException) {
                    null
                }
            }
            if (signatureBitmap != null) {
                binding.btnSign.setImageBitmap(signatureBitmap)
                binding.btnSwitch.isChecked = true
            }
        }
    }

    private fun onDeleteClick(position: Int) {

        AlertDialog.Builder(this)
            .setTitle("Delete Product")
            .setMessage("Are you sure you want to delete this product?")
            .setPositiveButton("Delete") { _, _ ->
                listOfProducts.removeAt(position)
                setupRecyclerView(listOfProducts)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun onItemClick(selectedProductModel: SelectedProductModel) {
    }

    override fun onStart() {
        super.onStart()

        viewModel.addCreditNoteResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Credit Note Saved successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error adding debit note: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    // show a progress dialog or something similar
                    ProgressBarUtil.showProgressBar(binding.progressBarLayout)
                }
                else -> {}
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addCreditNoteResult.removeObservers(this)
    }

    private fun saveData() {

        val message = checkValidation()

        billFinalAmount = etProductTotalAmount.text.toString().toDouble() + freightCharge + InsuranceCharge + LoadingCharge
        + PackagingCharge + OtherCharge

        if (message == Constants.OK) {

            if (binding.btnSwitch.isChecked) {
                val baos = ByteArrayOutputStream()
                signatureBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                bitmapByteData = baos.toByteArray()
            }

            viewModel.addCreditNote(
                CreditNoteModel(
                    amount = binding.etProductAmount.text.toString(),
                    billFinalAmount=billFinalAmount,
                    buyerDetail = buyerModel,
                    cess = binding.etProductCESS.text.toString(),
                    consigneeDetailType = when {
                        binding.r2b1.isChecked -> "Show Consignee (Same as above)"
                        binding.r2b2.isChecked -> "Consignee Not Required"
                        binding.r2b3.isChecked -> "Add Consignee (If different from above)"
                        else -> "No radio button is checked"
                    },
                    consigneeModel=when {
                        binding.r2b1.isChecked -> ConsigneeModel(buyerModel)
                        binding.r2b2.isChecked -> ConsigneeModel()
                        binding.r2b3.isChecked -> consigneeModel
                        else -> {
                            ConsigneeModel()
                        }
                    },
                    gst = etProductGST.text.toString(),
                    imageUrl = supplierModel.imageUrl,
                    creditNoteCode = creditNoteCode,
                    creditNoteDate = binding.etCreditNoteDate.text.toString(),
                    creditNoteNumber = binding.etCreditNoteNumber.text.toString().ifBlank { "1" },
                    buyerId = buyerId,
                    taxableAmount = etTaxableAmount.text.toString(),
                    termsAndCondition = etTermsAndConditions.text.toString(),
                    totalAmount = etProductTotalAmount.text.toString(),
                    totalTax = etProductTotalTax.text.toString(),
                    productDetails = listOfProducts,
                    supplierDetail = supplierModel,
                    transportDetails = transportModel,
                ),
                isForUpdate = isForUpdate,
                documentId = docId,
                bitmapByteData,
                previousBillFinalAmount
            )
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkValidation(): String {
        if (supplierModel.firmName.isNullOrEmpty())
            return "Select Supplier"
        if (buyerModel.companyName.isNullOrEmpty())
            return "Select Buyer"
        if (listOfProducts.isEmpty())
            return "Select atleast one product"
        if (binding.etCreditNoteDate.text.isNullOrEmpty())
            return "Please Select Purchase Date"
        if (binding.btnSwitch.isChecked)
            if (signatureBitmap == null)
                return "Please Add Signature"
        return Constants.OK
    }


    private fun setSupplierData() {
        binding.apply {
            btnAddSupplierDetails.visibility = View.INVISIBLE
            supplierAddedLayout.visibility = View.VISIBLE
            supplierCompanyName.visibility = View.VISIBLE
            supplierCompanyName.text = supplierModel.firmName
            if (!supplierModel.address.isNullOrBlank()) {
                supplierAddress.visibility = View.VISIBLE
                supplierAddress.text = supplierModel.address
            }
            if (!supplierModel.city.isNullOrBlank() || !supplierModel.pincode.isNullOrBlank()) {
                supplierCity.visibility = View.VISIBLE
                supplierCity.text = supplierModel.city + "," + supplierModel.pincode
            }
            if (!supplierModel.state.isNullOrBlank()) {
                supplierState.visibility = View.VISIBLE
                supplierState.text = supplierModel.state
            }

        }
    }

    private fun setConsigneeVisibility(boolean: Boolean) {
        if (boolean){
            addConsigneeLayout.visibility = View.VISIBLE
            if(consigneeModel.companyName.isNullOrEmpty()){
                btnConsigneeDetails.visibility = View.VISIBLE
                consignee_added_layout.visibility = View.INVISIBLE
            }else
                setConsigneeData()
        }else{
            addConsigneeLayout.visibility= View.INVISIBLE
        }
    }

    private fun setConsigneeData() {
        binding.apply {
            btnConsigneeDetails.visibility = View.INVISIBLE
            consignee_added_layout.visibility = View.VISIBLE
            consigneeName.visibility = View.VISIBLE
            consigneeName.text = consigneeModel.companyName
            if (!consigneeModel.address.isNullOrBlank()) {
                consigneeAddress.visibility = View.VISIBLE
                consigneeAddress.text = consigneeModel.address
            }
            if (!consigneeModel.city.isNullOrBlank() || !consigneeModel.pincode.isNullOrBlank()) {
                consigneeCity.visibility = View.VISIBLE
                consigneeCity.text = consigneeModel.city + "," + consigneeModel.pincode
            }
            if (!consigneeModel.state.isNullOrBlank()) {
                consigneeState.visibility = View.VISIBLE
                consigneeState.text = consigneeModel.state
            }
            if (!consigneeModel.email.isNullOrBlank()) {
                consigneeEmail.visibility = View.VISIBLE
                consigneeEmail.text = consigneeModel.email
            }

        }
    }

    private fun setBuyerData() {
        binding.apply {
            btnBuyerDetails.visibility = View.INVISIBLE
            buyerAddedLayout.visibility = View.VISIBLE
            buyerName.visibility = View.VISIBLE
            buyerName.text = buyerModel.companyName
            if (!buyerModel.address.isNullOrBlank()) {
                buyerAddress.visibility = View.VISIBLE
                buyerAddress.text = buyerModel.address
            }
            if (!buyerModel.city.isNullOrBlank() || !buyerModel.pincode.isNullOrBlank()) {
                buyerCity.visibility = View.VISIBLE
                buyerCity.text = buyerModel.city + "," + buyerModel.pincode
            }
            if (!buyerModel.state.isNullOrBlank()) {
                buyerState.visibility = View.VISIBLE
                buyerState.text = buyerModel.state
            }
            if (!buyerModel.email.isNullOrBlank()) {
                buyerEmail.visibility = View.VISIBLE
                buyerEmail.text = buyerModel.email
            }

        }
    }

    private fun setProductData() {
        setupRecyclerView(listOfProducts)
    }

    private fun setTransportData() {
        binding.apply {
            if (!transportModel.dateOfSupply.isNullOrEmpty()) {
                btnTransportDetails.visibility = View.GONE
                transportDataLayout.visibility = View.VISIBLE
                transportSupplyDate.visibility = View.VISIBLE
                transportSupplyDate.text = "Date of Supply : ${transportModel.dateOfSupply}"
                if (!transportModel.transportationMode.isNullOrBlank()) {
                    transportMode.visibility = View.VISIBLE
                    transportMode.text = "Transport Mode : ${transportModel.transportationMode}"
                }
                if (!transportModel.transportName.isNullOrBlank()) {
                    transportTranporterName.visibility = View.VISIBLE
                    transportTranporterName.text =
                        "Transport Name : ${transportModel.transportName}"
                }
            }
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                when (data?.getStringExtra(Constants.INTENT_ACTIVITY)) {

                    Constants.INTENT_SUPPLIER_ACTIVITY -> {
                        supplierModel =
                            data.getParcelableExtra<SupplierModel>(Constants.INTENT_MODEL)!!
                        setSupplierData()
                    }

                    Constants.INTENT_BUYER_ACTIVITY -> {
                        buyerModel = data.getParcelableExtra<BuyerModel>(Constants.INTENT_MODEL)!!
                        buyerId = data.getStringExtra(Constants.INTENT_BUYER_ID)!!
                        setBuyerData()
                    }

                    Constants.INTENT_CONSIGNEE_ACTIVITY -> {
                        consigneeModel = data.getParcelableExtra<ConsigneeModel>(Constants.INTENT_MODEL)!!
                        setConsigneeData()
                    }

                    Constants.INTENT_PRODUCT_ACTIVITY -> {
                        selectedProductModel =
                            data.getParcelableExtra<SelectedProductModel>(Constants.INTENT_MODEL)!!
                        listOfProducts.add(selectedProductModel)
                        setProductData()
                    }
                    Constants.INTENT_TRANSPORT_ACTIVITY -> {
                        transportModel = data.getParcelableExtra<TransportModel>(Constants.INTENT_MODEL)!!
                        setTransportData()
                    }
                }
            }
        }

    private fun setupRecyclerView(list: List<SelectedProductModel>) {
        if (list.isEmpty()) {
            binding.productRecyclerView.visibility = View.INVISIBLE
            binding.productDetailAmountLayout.visibility = View.INVISIBLE
        } else {
            binding.productRecyclerView.visibility = View.VISIBLE
            productAdapter.setCreditNoteProductList(list)
            binding.productDetailAmountLayout.visibility = View.VISIBLE
            setupProductDetailAmount(list)
        }
    }


    private fun setupProductDetailAmount(list: List<SelectedProductModel>) {
        totalGST = 0.0
        totalCESS = 0.0
        totalAmount = 0.0
        totalTaxableAmount = 0.0
        totalTotalTax = 0.0
        totalTotalAmount = 0.0
        for (product in list) {

            // Update the total values
            totalGST += product.gstValue ?: 0.0
            totalCESS += product.cessValue ?: 0.0
            totalAmount += product.amount ?: 0.0
            totalTaxableAmount += product.taxableAmount ?: 0.0
            totalTotalTax += (product.gstValue ?: 0.0) + (product.cessValue ?: 0.0)
            totalTotalAmount += product.totalAmount ?: 0.0
        }

// Display the total values
        binding.apply {
            etProductGST.text = totalGST.toString()
            etProductCESS.text = totalCESS.toString()
            etProductAmount.text = totalAmount.toString()
            etTaxableAmount.text = totalTaxableAmount.toString()
            etProductTotalTax.text = totalTotalTax.toString()
            etProductTotalAmount.text = totalTotalAmount.toString()
        }
    }

    private fun openDialog() {
        val dialog = Dialog(this, R.style.Theme_Black_NoTitleBar_Fullscreen)
        val signatureBinding = SignatureDialogBinding.inflate(layoutInflater)
        dialog.setContentView(signatureBinding.root)
        dialog.setTitle("Sign Here")

        signatureBinding.signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {
                // Called when the user starts signing
            }

            override fun onSigned() {
                // Called when the signature pad is signed
            }

            override fun onClear() {
                // Called when the signature pad is cleared
            }
        })

        signatureBinding.cancelButton.setOnClickListener { dialog.dismiss() }

        signatureBinding.saveButton.setOnClickListener {
            signatureBitmap = signatureBinding.signaturePad.signatureBitmap
            binding.btnSign.setImageBitmap(signatureBitmap)
            dialog.dismiss()
        }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        dialog.show()
    }


}