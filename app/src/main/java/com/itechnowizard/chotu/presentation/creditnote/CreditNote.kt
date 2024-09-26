package com.itechnowizard.chotu.presentation.creditnote

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
import com.itechnowizard.chotu.databinding.ActivityCreditNoteBinding
import com.itechnowizard.chotu.domain.model.CreditNoteModel
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.presentation.creditnote.addcredit.AddCredit
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CreditNote : AppCompatActivity() {

    private lateinit var binding: ActivityCreditNoteBinding
    private val viewModel: CreditNoteViewModel by viewModels()
    private lateinit var adapter: CreditNoteAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfBuyer = mutableListOf<CreditNoteModel>()
    private var removedPositon: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = CreditNoteAdapter(this::onPreviewClick,this::onDeleteClick,this::onItemClick)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            recyclerView.adapter = adapter
            ToolbarUtils.setToolbar(toolbarLayout,true, Constants.CREDITNOTE, Constants.TOOLBAR_CREATE_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { startActivity(
                Intent(this@CreditNote,
                    AddCredit::class.java)
            ) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadCreditNoteList()

        viewModel.creditNoteListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfBuyer = state.list.toObjects<CreditNoteModel>() as MutableList<CreditNoteModel>
                setupRecyclerView(listOfBuyer)
            }
        }

        viewModel.removeCreditNoteResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "CreditNote details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfBuyer.removeAt(removedPositon)
                    adapter.setCreditNoteList(listOfBuyer)

                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting CreditNote details: ${result.message}",
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

    private fun onPreviewClick(invoiceModel: CreditNoteModel) {
        //   createPdfInvoice(invoiceModel)
        if (!AppPermission.permissionGranted(this)) AppPermission.requestPermission(this)
        else PdfUtils.createPdfForCreditNote(
            context = this,
            receipt = invoiceModel,
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

    private fun onItemClick(creditNoteModel: CreditNoteModel, position: Int) {

        val intent = Intent(this, AddCredit::class.java).apply {
            putExtra(Constants.INTENT_MODEL, creditNoteModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA,true)
        }
        startActivity(intent)
    }

    private fun onDeleteClick(buyerId: String,position: Int) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id,buyerId)
    }

    private fun showDeleteDialog(documentId: String,buyerId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this creditNote?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteCreditNoteDocument(documentId,buyerId)
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

    private fun setupRecyclerView(list: List<CreditNoteModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setCreditNoteList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addCreditNoteResult.removeObservers(this)
        viewModel.removeCreditNoteResult.removeObservers(this)
    }
}