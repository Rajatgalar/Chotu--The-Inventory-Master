package com.itechnowizard.chotu.presentation.payment.made

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.databinding.ActivityPaymentMadeBinding
import com.itechnowizard.chotu.domain.model.PaymentMadeModel
import com.itechnowizard.chotu.domain.model.PaymentReceiptModel
import com.itechnowizard.chotu.presentation.payment.made.addpayment.AddPaymentMade
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class PaymentMade : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentMadeBinding
    private val viewModel: PaymentMadeViewModel by viewModels()
    private lateinit var adapter: PaymentMadeAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfBuyer = mutableListOf<PaymentMadeModel>()
    private var removedPositon: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMadeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = PaymentMadeAdapter(this::onPreviewClick,this::onDeleteClick,this::onItemClick)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            recyclerView.adapter = adapter
            ToolbarUtils.setToolbar(toolbarLayout,true, Constants.PAYMENTRECEIPT, Constants.TOOLBAR_CREATE_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { startActivity(
                Intent(this@PaymentMade,
                    AddPaymentMade::class.java)
            ) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }


    override fun onStart() {
        super.onStart()
        viewModel.loadPaymentMadeList()

        viewModel.receiptListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfBuyer = state.list.toObjects<PaymentMadeModel>() as MutableList<PaymentMadeModel>
                setupRecyclerView(listOfBuyer)
            }
        }

        viewModel.removePaymentMadeResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Payment details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfBuyer.removeAt(removedPositon)
                    adapter.setPaymentMadeList(listOfBuyer)

                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting Payment details: ${result.message}",
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

    private fun onPreviewClick(receiptModel: PaymentMadeModel) {

        if (!AppPermission.permissionGranted(this)) AppPermission.requestPermission(this)
        else PdfUtils.createPdfForPaymentMade(
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

    private fun onItemClick(receiptModel: PaymentMadeModel, position: Int) {

        val intent = Intent(this, AddPaymentMade::class.java).apply {
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
                viewModel.deletePaymentMadeDocument(documentId,buyerId)
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

    private fun setupRecyclerView(list: List<PaymentMadeModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setPaymentMadeList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addPaymentMadeResult.removeObservers(this)
        viewModel.removePaymentMadeResult.removeObservers(this)
    }


}