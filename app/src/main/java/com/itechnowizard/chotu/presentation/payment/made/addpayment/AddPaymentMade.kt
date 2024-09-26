package com.itechnowizard.chotu.presentation.payment.made.addpayment

import android.R
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.itechnowizard.chotu.databinding.ActivityAddPaymentMadeBinding
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.SellerModel
import com.itechnowizard.chotu.domain.model.PaymentMadeModel
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.presentation.lists.seller.Seller
import com.itechnowizard.chotu.presentation.lists.supplier.SupplierViewModel
import com.itechnowizard.chotu.presentation.payment.made.PaymentMadeViewModel
import com.itechnowizard.chotu.presentation.payment.made.addpayment.purchaselist.PurchaseList
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_quotation.*

@AndroidEntryPoint
class AddPaymentMade : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddPaymentMadeBinding
    private val viewModel: PaymentMadeViewModel by viewModels()
    private val supplierViewModel: SupplierViewModel by viewModels()
    private var sellerID = ""
    private var docId: String = ""  /// this is payment Receipt document Id
    private var sellerPurchaseModel = BuyerSellerLedgerModel()
    private var isForUpdate: Boolean = false
    private var previousBillFinalAmount = 0.0
    private var purchaseLink = ""
    private var supplierDetailModel = SupplierModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPaymentMadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isForUpdate = intent.getBooleanExtra(Constants.IS_INTENT_HAVING_DATA, false)

        if (isForUpdate) {
            setDataForEdit()
        } else
            setDataForAdd()

        binding.apply {

            setSupportActionBar(toolbarLayout.toolbar)

            btnSellerDetails.setOnClickListener { startNewActivityForResult(Seller::class.java) }
            sellerName.setOnClickListener { startNewActivityForResult(Seller::class.java) }

            etPaymentMode.setAdapter(
                ArrayAdapter(
                    this@AddPaymentMade,
                    R.layout.simple_spinner_item,
                    resources.getStringArray(com.itechnowizard.chotu.R.array.PaymentMode)
                )
            )

            etPaymentTreatment.setAdapter(
                ArrayAdapter(
                    this@AddPaymentMade,
                    R.layout.simple_spinner_item,
                    resources.getStringArray(com.itechnowizard.chotu.R.array.PaymentTreatment)
                )
            )

            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }

            btnSave.setOnClickListener { saveData() }

            etPurchaseList.setOnClickListener {
                if(sellerID.isEmpty())
                    Toast.makeText(this@AddPaymentMade,"Select Seller First", Toast.LENGTH_SHORT).show()
                else
                    startForResult.launch(
                        Intent(this@AddPaymentMade, PurchaseList::class.java)
                            .putExtra("sellerId",sellerID)
                    )
            }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            etDebitNoteDate.setOnClickListener {
                DateUtils.showDatePicker(this@AddPaymentMade) { selectedDate ->
                    binding.etDebitNoteDate.setText(selectedDate)
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        supplierViewModel.loadSupplierDetails()
        viewModel.addPaymentMadeResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Payment Saved successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error adding Payment: ${result.message}",
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

        supplierViewModel.supplyDetailsState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.data != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                supplierDetailModel = state.data
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addPaymentMadeResult.removeObservers(this)
        supplierViewModel.supplyDetailsState.removeObservers(this)
    }


    private fun setDataForEdit() {

        val model = intent.getParcelableExtra<PaymentMadeModel>(Constants.INTENT_MODEL)!!

        docId = intent.getStringExtra(Constants.DOCUMENT_ID)!!

        sellerID = model.sellerId!!
        previousBillFinalAmount = model.totalAmount!!

        binding.btnSellerDetails.visibility = View.GONE
        binding.sellerName.visibility = View.VISIBLE

        binding.apply {
            etDebitNoteDate.setText(model.paymentDate)
            etDebitNoteNumber.setText(model.receiptNumber)
            sellerName.text = model.sellerName
            etAmount.setText(model.totalAmount.toString())
            etPaymentTreatment.setText(model.treatment)
            etPaymentMode.setText(model.paymentMode)
            etPurchaseList.setText(" Purchase Number  #${sellerPurchaseModel.invoiceNumber}")
            etAccountType.setText(model.accountType)
            etNote.setText(model.remarks)
        }
    }

    private fun saveData() {

        val message = checkValidation()

        if (message == Constants.OK) {

            viewModel.addPaymentMade(
                PaymentMadeModel(
                    accountType = binding.etAccountType.text.toString(),
                    sellerId = sellerID,
                    sellerName = binding.sellerName.text.toString(),
                    purchaseLink = purchaseLink,
                    purchaseNumber = sellerPurchaseModel.invoiceNumber,
                    paymentDate = binding.etDebitNoteDate.text.toString(),
                    paymentMode = binding.etPaymentMode.text.toString(),
                    receiptNumber = binding.etDebitNoteNumber.text.toString().ifBlank { "1" },
                    remarks = binding.etNote.text.toString(),
                    supplierDetail = supplierDetailModel,
                    totalAmount = binding.etAmount.text.toString().toDouble(),
                    treatment = binding.etPaymentTreatment.text.toString()
                ),
                isForUpdate = isForUpdate,
                documentId = docId,
                previousBillFinalAmount=previousBillFinalAmount
            )
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun checkValidation(): String {
        if (sellerID.isNullOrEmpty())
            return "Select Seller"
        if (binding.etAmount.text.isNullOrEmpty())
            return "Enter Amount"
        if (binding.etPurchaseList.text.isEmpty())
            return "Select Purchase"
        return Constants.OK
    }

    private fun setDataForAdd() {
        binding.etDebitNoteDate.setText(DateUtils.getTodayDate())
        ToolbarUtils.setToolbar(
            binding.toolbarLayout, true,
            Constants.TOOLBAR_Edit_RECEIPT,
            Constants.TOOLBAR_SAVE
        )
        binding.etPaymentMode.setText(resources.getStringArray(com.itechnowizard.chotu.R.array.PaymentMode)[0], false)
        binding.etPaymentTreatment.setText(resources.getStringArray(com.itechnowizard.chotu.R.array.PaymentTreatment)[1], false)
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                when (result.data?.getStringExtra(Constants.INTENT_ACTIVITY)) {
                    Constants.INTENT_SELLER_ACTIVITY -> {
                        val sellerModel = result.data!!.getParcelableExtra<SellerModel>(Constants.INTENT_MODEL)!!
                        sellerID = result.data!!.getStringExtra(Constants.INTENT_SELLER_ID)!!
                        binding.apply {
                            btnSellerDetails.visibility = View.GONE
                            sellerName.visibility = View.VISIBLE
                            sellerName.text = sellerModel.companyName
                        }
                    }

                    Constants.INTENT_INVOICE_LIST_ACTIVITY ->{
                        sellerPurchaseModel = result.data!!.getParcelableExtra<BuyerSellerLedgerModel>(
                            Constants.INTENT_MODEL)!!
                        binding.etPurchaseList.setText(" Purchase Number  #${sellerPurchaseModel.invoiceNumber}")
                        purchaseLink = result.data!!.getStringExtra(Constants.DOCUMENT_ID).toString()
                    }
                }
            }
        }

    private fun startNewActivityForResult(destination: Class<*>) {
        startForResult.launch(Intent(this, destination))
    }
}
