package com.itechnowizard.chotu.presentation.payment.receipt

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.databinding.ActivityPaymentReceiptBinding
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.PaymentReceiptModel
import com.itechnowizard.chotu.presentation.payment.receipt.addpayment.AddPaymentReceipt
import com.itechnowizard.chotu.utils.*
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class PaymentReceipt : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentReceiptBinding
    private val viewModel: PaymentReceiptViewModel by viewModels()
    private lateinit var adapter: PaymentReceiptAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfBuyer = mutableListOf<PaymentReceiptModel>()
    private var removedPositon: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PaymentReceiptAdapter(this::onPreviewClick,this::onDeleteClick,this::onItemClick)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            recyclerView.adapter = adapter
            ToolbarUtils.setToolbar(toolbarLayout,true, Constants.PAYMENTRECEIPT, Constants.TOOLBAR_CREATE_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { startActivity(
                Intent(this@PaymentReceipt,
                    AddPaymentReceipt::class.java)
            ) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.loadPaymentReceiptList()

        viewModel.receiptListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfBuyer = state.list.toObjects<PaymentReceiptModel>() as MutableList<PaymentReceiptModel>
                setupRecyclerView(listOfBuyer)
            }
        }

        viewModel.removePaymentReceiptResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Payment Receipt details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfBuyer.removeAt(removedPositon)
                    adapter.setPaymentReceiptList(listOfBuyer)

                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting Payment Receipt details: ${result.message}",
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

    private fun onPreviewClick(receiptModel: PaymentReceiptModel) {

        if (!AppPermission.permissionGranted(this)) AppPermission.requestPermission(this)
        else PdfUtils.createPdfForPaymentReceipt(
            context = this,
            receipt = receiptModel,
            onFinish = this::openFile,
            onError = { toastErrorMessage(it.message.toString()) }
        )
    }

    private fun openFile(file: File) {
        PdfUtils.openFile(this, file, "com.adobe.reader")
    }

    private fun toastErrorMessage(s: String) {
        PdfUtils.toastErrorMessage(this, s)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppPermission.REQUEST_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AppPermission.requestPermission(this)
                toastErrorMessage("Permission should be allowed")
            }else{
                toastErrorMessage("Permission is granted")
            }
        }
    }


    private fun onItemClick(receiptModel: PaymentReceiptModel, position: Int) {

        val intent = Intent(this, AddPaymentReceipt::class.java).apply {
            putExtra(Constants.INTENT_MODEL, receiptModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA,true)
        }
        startActivity(intent)
    }

    private fun onDeleteClick(position: Int,buyerId: String) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id,buyerId)
    }

    private fun showDeleteDialog(documentId: String,buyerId : String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this receipt?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deletePaymentReceiptDocument(documentId,buyerId)
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

    private fun setupRecyclerView(list: List<PaymentReceiptModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setPaymentReceiptList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addPaymentReceiptResult.removeObservers(this)
        viewModel.removePaymentReceiptResult.removeObservers(this)
    }
    
}