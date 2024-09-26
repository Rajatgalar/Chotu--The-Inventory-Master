package com.itechnowizard.chotu.presentation.quotation

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
import com.itechnowizard.chotu.databinding.ActivityQuotationBinding
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.QuotationModel
import com.itechnowizard.chotu.presentation.quotation.addquotation.AddQuotation
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class Quotation : AppCompatActivity() {

    private lateinit var binding: ActivityQuotationBinding
    private val viewModel: QuotationViewModel by viewModels()
    private lateinit var adapter: QuotationAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfBuyer = mutableListOf<QuotationModel>()
    private var removedPositon: Int = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuotationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = QuotationAdapter(this::onPreviewClick,this::onDeleteClick, this::onItemClick)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            recyclerView.adapter = adapter
            ToolbarUtils.setToolbar(
                toolbarLayout,
                true,
                Constants.QUOTATION,
                Constants.TOOLBAR_CREATE_NEW
            )

            toolbarLayout.toolbarMenuText.setOnClickListener {
                startActivity(
                    Intent(
                        this@Quotation,
                        AddQuotation::class.java
                    )
                )
            }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter, adapter)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadQuotationList()

        viewModel.quotationListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfBuyer = state.list.toObjects<QuotationModel>() as MutableList<QuotationModel>
                setupRecyclerView(listOfBuyer)
            }
        }

        viewModel.removeQuotationResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Quotation details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfBuyer.removeAt(removedPositon)
                    adapter.setQuotationList(listOfBuyer)
                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting Quotation details: ${result.message}",
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

    private fun onPreviewClick(invoiceModel: QuotationModel) {
        //   createPdfInvoice(invoiceModel)
        if (!AppPermission.permissionGranted(this)) AppPermission.requestPermission(this)
        else PdfUtils.createPdfForQuotation(
            context = this,
            invoiceModel = invoiceModel,
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


    private fun onItemClick(quotationModel: QuotationModel, position: Int) {

        val intent = Intent(this, AddQuotation::class.java).apply {
            putExtra(Constants.INTENT_MODEL, quotationModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA, true)
        }
        startActivity(intent)
    }

    private fun onDeleteClick(position: Int) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id)
    }

    private fun showDeleteDialog(documentId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this quotation?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteQuotationDocument(documentId)
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

    private fun setupRecyclerView(list: List<QuotationModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setQuotationList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addQuotationResult.removeObservers(this)
        viewModel.removeQuotationResult.removeObservers(this)
    }
}