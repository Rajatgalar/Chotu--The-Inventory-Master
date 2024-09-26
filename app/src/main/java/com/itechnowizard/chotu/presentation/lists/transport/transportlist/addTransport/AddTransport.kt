package com.itechnowizard.chotu.presentation.lists.transport.transportlist.addTransport

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.itechnowizard.chotu.databinding.ActivityAddtransportBinding
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.presentation.lists.transport.transportlist.TransportListViewModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.Resource
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTransport : AppCompatActivity() {

    private lateinit var binding: ActivityAddtransportBinding
    private val viewModel: TransportListViewModel by viewModels()

    private var isForUpdate : Boolean = false
    private var DocumentId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddtransportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_ADD_TRANSPORT,Constants.TOOLBAR_SAVE)


            toolbarLayout.toolbarBack.setOnClickListener { finish() }
            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }
            btnSave.setOnClickListener { saveData() }

            viewModel.addTransportResult.observe(this@AddTransport) { result ->
                when (result) {
                    is Resource.Success -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(this@AddTransport, "Transport added successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is Resource.Error -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(
                            this@AddTransport,
                            "Error adding Transport details: ${result.message}",
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
    }

    private fun saveData() {
        val message = checkValidation()
        if (message == Constants.OK) {
            viewModel.addTransport(
                TransportListModel(
                    name = binding.etTransportName.text.toString(),
                    transportId = binding.etTransportId.text.toString(),
                ),isForUpdate,DocumentId
            )
        } else {
            Toast.makeText(this@AddTransport, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkValidation(): String {
        if (binding.etTransportName.text.isNullOrEmpty())
            return "Enter Name"
        return Constants.OK
    }

    override fun onStop() {
        super.onStop()
        viewModel.addTransportResult.removeObservers(this@AddTransport)
    }
}