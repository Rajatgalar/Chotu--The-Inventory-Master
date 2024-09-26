package com.itechnowizard.chotu.presentation.payment.receipt.addpayment

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
import com.bumptech.glide.Glide
import com.itechnowizard.chotu.databinding.ActivityAddPaymentReceiptBinding
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.presentation.lists.buyer.Buyer
import com.itechnowizard.chotu.presentation.lists.supplier.SupplierViewModel
import com.itechnowizard.chotu.presentation.payment.receipt.PaymentReceiptViewModel
import com.itechnowizard.chotu.presentation.payment.receipt.addpayment.invoicelist.InvoiceList
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_credit.*

@AndroidEntryPoint
class AddPaymentReceipt : AppCompatActivity() {

    private lateinit var binding: ActivityAddPaymentReceiptBinding
    private val viewModel: PaymentReceiptViewModel by viewModels()
    private val supplierViewModel: SupplierViewModel by viewModels()
    private var buyerID = ""
    private var docId: String = ""  /// this is payment Receipt document Id
    private var buyerSellerLedgerModel = BuyerSellerLedgerModel()
    private var isForUpdate: Boolean = false
    private var previousBillFinalAmount = 0.0
    private var invoiceLink = ""
    private var supplierDetailModel = SupplierModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPaymentReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        isForUpdate = intent.getBooleanExtra(Constants.IS_INTENT_HAVING_DATA, false)

        if (isForUpdate) {
            setDataForEdit()
        } else
            setDataForAdd()

        binding.apply {

            setSupportActionBar(toolbarLayout.toolbar)

            btnBuyerDetails.setOnClickListener { startNewActivityForResult(Buyer::class.java) }
            buyerName.setOnClickListener { startNewActivityForResult(Buyer::class.java) }

            etPaymentMode.setAdapter(
                ArrayAdapter(
                    this@AddPaymentReceipt,
                    R.layout.simple_spinner_item,
                    resources.getStringArray(com.itechnowizard.chotu.R.array.PaymentMode)
                )
            )

            etPaymentTreatment.setAdapter(
                ArrayAdapter(
                    this@AddPaymentReceipt,
                    R.layout.simple_spinner_item,
                    resources.getStringArray(com.itechnowizard.chotu.R.array.PaymentTreatment)
                )
            )

            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }

            btnSave.setOnClickListener { saveData() }

            etInvoiceList.setOnClickListener {
                if(buyerID.isEmpty())
                    Toast.makeText(this@AddPaymentReceipt,"Select Buyer First",Toast.LENGTH_SHORT).show()
                else
                    startForResult.launch(
                        Intent(this@AddPaymentReceipt, InvoiceList::class.java)
                            .putExtra("buyerId",buyerID)
                )
            }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            etDebitNoteDate.setOnClickListener {
                DateUtils.showDatePicker(this@AddPaymentReceipt) { selectedDate ->
                    binding.etDebitNoteDate.setText(selectedDate)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        supplierViewModel.loadSupplierDetails()
        viewModel.addPaymentReceiptResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Receipt Saved successfully", Toast.LENGTH_SHORT)
                        .show()
                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error adding Receipt: ${result.message}",
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
        viewModel.addPaymentReceiptResult.removeObservers(this)
        supplierViewModel.supplyDetailsState.removeObservers(this)
    }


    private fun setDataForEdit() {

        val model = intent.getParcelableExtra<PaymentReceiptModel>(Constants.INTENT_MODEL)!!

        docId = intent.getStringExtra(Constants.DOCUMENT_ID)!!

        buyerID = model.buyerId!!
        previousBillFinalAmount = model.totalAmount!!

        btnBuyerDetails.visibility = View.GONE
        buyerName.visibility = View.VISIBLE

        binding.apply {
            etDebitNoteDate.setText(model.paymentDate)
            etDebitNoteNumber.setText(model.receiptNumber)
            buyerName.text = model.buyerName
            etAmount.setText(model.totalAmount.toString())
            etPaymentTreatment.setText(model.treatment)
            etPaymentMode.setText(model.paymentMode)
            etInvoiceList.setText(" Invoice Number  #${buyerSellerLedgerModel.invoiceNumber}")
            etAccountType.setText(model.accountType)
            etNote.setText(model.remarks)
        }
    }

    private fun saveData() {

        val message = checkValidation()

        if (message == Constants.OK) {

            viewModel.addPaymentReceipt(
                PaymentReceiptModel(
                    accountType = binding.etAccountType.text.toString(),
                    buyerId = buyerID,
                    buyerName = binding.buyerName.text.toString(),
                    invoiceLink = invoiceLink,
                    invoiceNumber = buyerSellerLedgerModel.invoiceNumber,
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
        if (buyerID.isNullOrEmpty())
            return "Select Buyer"
        if (binding.etAmount.text.isNullOrEmpty())
            return "Enter Amount"
        if (binding.etInvoiceList.text.isEmpty())
            return "Select Invoice"
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
                        Constants.INTENT_BUYER_ACTIVITY -> {
                            val buyerModel = result.data!!.getParcelableExtra<BuyerModel>(Constants.INTENT_MODEL)!!
                            buyerID = result.data!!.getStringExtra(Constants.INTENT_BUYER_ID)!!
                            binding.apply {
                                btnBuyerDetails.visibility = View.GONE
                                buyerName.visibility = View.VISIBLE
                                buyerName.text = buyerModel.companyName
                            }
                        }

                        Constants.INTENT_INVOICE_LIST_ACTIVITY ->{
                            buyerSellerLedgerModel = result.data!!.getParcelableExtra<BuyerSellerLedgerModel>(Constants.INTENT_MODEL)!!
                            binding.etInvoiceList.setText(" Invoice Number  #${buyerSellerLedgerModel.invoiceNumber}")
                            invoiceLink = result.data!!.getStringExtra(Constants.DOCUMENT_ID).toString()
                        }
                    }
                }
            }

    private fun startNewActivityForResult(destination: Class<*>) {
        startForResult.launch(Intent(this, destination))
    }


}