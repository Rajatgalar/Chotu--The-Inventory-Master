package com.itechnowizard.chotu.presentation.invoice

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.itechnowizard.chotu.BuildConfig
import com.itechnowizard.chotu.databinding.ActivityInvoiceBinding
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.presentation.invoice.addinvoice.AddInvoiceActivity
import com.itechnowizard.chotu.utils.*
import com.itechnowizard.chotu.utils.AppPermission.Companion.permissionGranted
import com.itechnowizard.chotu.utils.AppPermission.Companion.requestPermission
import com.itechnowizard.chotu.utils.Pdf.PdfInvoice
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class Invoice : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceBinding
    private val viewModel: InvoiceViewModel by viewModels()
    private lateinit var adapter: InvoiceAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfBuyer = mutableListOf<InvoiceModel>()
    private var removedPositon: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = InvoiceAdapter(this::onPreviewClick,this::onDeleteClick,this::onItemClick)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            recyclerView.adapter = adapter
            ToolbarUtils.setToolbar(toolbarLayout,true,Constants.INVOICE,Constants.TOOLBAR_CREATE_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { startActivity(Intent(this@Invoice,AddInvoiceActivity::class.java)) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadInvoiceList()

        viewModel.invoiceListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfBuyer = state.list.toObjects<InvoiceModel>() as MutableList<InvoiceModel>
                setupRecyclerView(listOfBuyer)
            }
        }

        viewModel.removeInvoiceResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Invoice details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfBuyer.removeAt(removedPositon)
                    adapter.setInvoiceList(listOfBuyer)
                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting Invoice details: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    ProgressBarUtil.showProgressBar(binding.progressBarLayout)
                    // show a progress dialog or something similar
                }
                else -> {}
            }
        }
    }

    private fun onPreviewClick(invoiceModel: InvoiceModel) {
        if (!permissionGranted(this)) requestPermission(this)
        else PdfUtils.createPdfForInvoice(
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
                requestPermission(this)
                toastErrorMessage("Permission should be allowed")
            }else{
                toastErrorMessage("Permission is granted")
            }
        }
    }

    private fun onItemClick(invoiceModel: InvoiceModel, position: Int) {

        val intent = Intent(this, AddInvoiceActivity::class.java).apply {
            putExtra(Constants.INTENT_MODEL, invoiceModel)
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
            .setMessage("Are you sure you want to delete this invoice?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteInvoiceDocument(documentId,buyerId)
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

    private fun setupRecyclerView(list: List<InvoiceModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setInvoiceList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addInvoiceResult.removeObservers(this)
        viewModel.removeInvoiceResult.removeObservers(this)
    }
}