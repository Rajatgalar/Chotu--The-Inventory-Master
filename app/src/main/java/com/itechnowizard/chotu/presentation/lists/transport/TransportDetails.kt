package com.itechnowizard.chotu.presentation.lists.transport

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.itechnowizard.chotu.databinding.ActivityTransportDetailsBinding
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.model.TransportModel
import com.itechnowizard.chotu.presentation.lists.transport.transportlist.TransportList
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.DateUtils
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransportDetails : AppCompatActivity() {

    private lateinit var binding: ActivityTransportDetailsBinding
    private var transportListModel = TransportListModel()
    private var transportModel = TransportModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransportDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        transportModel = intent.getParcelableExtra<TransportModel>(Constants.INTENT_DATA)!!

        setData()

        binding.apply {

            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(
                toolbarLayout, true,
                Constants.TOOLBAR_TRANSPORTATION_DETAILS,
                Constants.TOOLBAR_SAVE
            )

            etTransportMode.setAdapter(
                ArrayAdapter(
                    this@TransportDetails,
                    R.layout.simple_spinner_item,
                    resources.getStringArray(com.itechnowizard.chotu.R.array.TransportMode)
                )
            )
            etSupplyType.setAdapter(
                ArrayAdapter(
                    this@TransportDetails,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(com.itechnowizard.chotu.R.array.TransportSupplyType)
                )
            )



            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }

            btnSave.setOnClickListener { saveData() }

            etTransporter.setOnClickListener { startNewActivityForResult(TransportList::class.java) }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

        }
    }

    private fun setData() {
        binding.apply {
            etTransportMode.setText(transportModel.transportationMode)
            etDocumentNumber.setText(transportModel.documentNumber)
            etVehicleNo.setText(transportModel.vehicleNumber)
            if (transportModel.dateOfSupply.isNullOrBlank())
                etDateOfSupply.setText(DateUtils.getTodayDate())
            else
                etDateOfSupply.setText(transportModel.dateOfSupply)
            etPLaceOfSupply.setText(transportModel.transportationMode)
            etTransporter.setText(transportModel.transportName)
            etSupplyType.setText(transportModel.supplyType)

            transportListModel.name = transportModel.transportName
            transportListModel.transportId = transportModel.transportId
        }
    }

    private fun startNewActivityForResult(destination: Class<TransportList>) {
        startForResult.launch(Intent(this, destination))
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    transportListModel =
                        result.data!!.getParcelableExtra<TransportListModel>(Constants.INTENT_MODEL)!!
                    binding.etTransporter.setText(transportListModel.name)
                }
            }
        }


    private fun saveData() {
        backToPreviousActivity()
    }

    private fun backToPreviousActivity() {
        val intent = Intent()
        intent.putExtra(
            Constants.INTENT_MODEL, TransportModel(
                dateOfSupply = binding.etDateOfSupply.text.toString(),
                documentNumber = binding.etDocumentNumber.text.toString(),
                placeOfSupply = binding.etPLaceOfSupply.text.toString(),
                supplyType = binding.etSupplyType.text.toString(),
                transportationMode = binding.etTransportMode.text.toString(),
                transportName = transportListModel.name,
                transportId = transportListModel.transportId,
                vehicleNumber = binding.etVehicleNo.text.toString(),
            )
        )
        intent.putExtra(Constants.INTENT_ACTIVITY, Constants.INTENT_TRANSPORT_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}


