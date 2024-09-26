package com.itechnowizard.chotu.presentation.debitnote

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
import com.itechnowizard.chotu.databinding.ActivityDebitNoteBinding
import com.itechnowizard.chotu.domain.model.CreditNoteModel
import com.itechnowizard.chotu.domain.model.DebitNoteModel
import com.itechnowizard.chotu.presentation.debitnote.adddebit.AddDebit
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class DebitNote : AppCompatActivity() {

    private lateinit var binding: ActivityDebitNoteBinding
    private val viewModel: DebitNoteViewModel by viewModels()
    private lateinit var adapter: DebitNoteAdapter
    private var documentSnapshot = mutableListOf<DocumentSnapshot>()
    private var listOfDebitNotes = mutableListOf<DebitNoteModel>()
    private var removedPositon: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebitNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = DebitNoteAdapter(this::onPreviewClick,this::onDeleteClick,this::onItemClick)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            recyclerView.adapter = adapter
            ToolbarUtils.setToolbar(toolbarLayout,true, Constants.DEBITNOTE, Constants.TOOLBAR_CREATE_NEW)

            toolbarLayout.toolbarMenuText.setOnClickListener { startActivity(
                Intent(this@DebitNote,
                    AddDebit::class.java)
            ) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            SearchViewUtil.setupSearchView(searchFilter,adapter)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadDebitNoteList()

        viewModel.debitNoteListState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.list != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                this.documentSnapshot = state.list.documents
                listOfDebitNotes = state.list.toObjects<DebitNoteModel>() as MutableList<DebitNoteModel>
                setupRecyclerView(listOfDebitNotes)
            }
        }

        viewModel.removeDebitNoteResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "DebitNote details Deleted successfully", Toast.LENGTH_SHORT)
                        .show()
                    listOfDebitNotes.removeAt(removedPositon)
                    adapter.setDebitNoteList(listOfDebitNotes)

                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error Deleting DebitNote details: ${result.message}",
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

    private fun onPreviewClick(invoiceModel: DebitNoteModel) {
        //   createPdfInvoice(invoiceModel)
        if (!AppPermission.permissionGranted(this)) AppPermission.requestPermission(this)
        else PdfUtils.createPdfForDebitNote(
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


    private fun onItemClick(debitNoteModel: DebitNoteModel, position: Int) {

        val intent = Intent(this, AddDebit::class.java).apply {
            putExtra(Constants.INTENT_MODEL, debitNoteModel)
            putExtra(Constants.DOCUMENT_ID, documentSnapshot[position].id)
            putExtra(Constants.IS_INTENT_HAVING_DATA,true)
        }
        startActivity(intent)
    }

    private fun onDeleteClick(sellerId: String,position: Int) {
        removedPositon = position
        showDeleteDialog(documentSnapshot[position].id,sellerId)
    }

    private fun showDeleteDialog(documentId: String,sellerId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Document")
            .setMessage("Are you sure you want to delete this debitNote?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteDebitNoteDocument(documentId,sellerId)
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

    private fun setupRecyclerView(list: List<DebitNoteModel>) {
        if (list.isEmpty()) {
            setVisiblityOfRecyclerView(false)
        } else {
            setVisiblityOfRecyclerView(true)
            adapter.setDebitNoteList(list)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.addDebitNoteResult.removeObservers(this)
        viewModel.removeDebitNoteResult.removeObservers(this)
    }
}