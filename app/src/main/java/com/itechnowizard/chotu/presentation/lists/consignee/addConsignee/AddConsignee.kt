package com.itechnowizard.chotu.presentation.lists.consignee.addConsignee

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityAddBuyerBinding
import com.itechnowizard.chotu.domain.model.ConsigneeModel
import com.itechnowizard.chotu.presentation.lists.buyer.BuyerViewModel
import com.itechnowizard.chotu.presentation.lists.consignee.ConsigneeViewModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.Resource
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddConsignee : AppCompatActivity() {

    private lateinit var binding: ActivityAddBuyerBinding
    private val viewModel: ConsigneeViewModel by viewModels()

    private var isForUpdate : Boolean = false
    private var DocumentId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBuyerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isForUpdate = intent.getBooleanExtra(Constants.IS_INTENT_HAVING_DATA,false)
        if(isForUpdate){
            setDataForEdit()
        }else
            setDataForAdd()

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)

            etState.setAdapter(
                ArrayAdapter(this@AddConsignee, R.layout.list_item,R.id.item_title,
                resources.getStringArray(R.array.india_states_array))
            )
            etGstTreatment.setAdapter(
                ArrayAdapter(this@AddConsignee, R.layout.list_item,R.id.item_title,
                    resources.getStringArray(R.array.gst_treatment_type))
            )

            toolbarLayout.toolbarBack.setOnClickListener { finish() }
            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }
            btnSave.setOnClickListener { saveData() }

            viewModel.addBuyerResult.observe(this@AddConsignee) { result ->
                when (result) {
                    is Resource.Success -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(this@AddConsignee, "Consignee details Saved successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is Resource.Error -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(
                            this@AddConsignee,
                            "Error adding Consignee details: ${result.message}",
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

    private fun setDataForAdd() {
        ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_ADD_CONSIGNEE,Constants.TOOLBAR_SAVE)
    }

    private fun setDataForEdit() {

        val model = intent.getParcelableExtra<ConsigneeModel>(Constants.INTENT_MODEL)!!
        DocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        binding.etCompanyName.setText(model.companyName)
        binding.etGSTIN.setText(model.gstin)
        binding.etEmail.setText(model.email)
        binding.etMobile.setText(model.mobile)
        binding.etAddress.setText(model.address)
        binding.etCity.setText(model.city)
        binding.etState.setText(model.state)
        binding.etGstTreatment.setText(model.gsttreatmentType)

        ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_Edit_CONSIGNEE,Constants.TOOLBAR_UPDATE)
        binding.btnSave.text = Constants.TOOLBAR_UPDATE

    }

    private fun saveData() {
        val message = checkValidation()
        if (message == Constants.OK) {
            viewModel.addBuyer(ConsigneeModel(
                    gstin = binding.etGSTIN.text.toString(),
                    companyName = binding.etCompanyName.text.toString(),
                    email = binding.etEmail.text.toString(),
                    mobile = binding.etMobile.text.toString(),
                    address = binding.etAddress.text.toString(),
                    city = binding.etCity.text.toString(),
                    state  = binding.etState.text.toString(),
                    gsttreatmentType = binding.etGstTreatment.text.toString()
                ),isForUpdate,DocumentId
            )
        } else {
            Toast.makeText(this@AddConsignee, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkValidation(): String {
        if (binding.etCompanyName.text.isNullOrEmpty())
            return "Enter Company Name"
        if (binding.etState.text.isNullOrEmpty())
            return "Please Select State"
        return Constants.OK
    }

    override fun onStop() {
        super.onStop()
        viewModel.addBuyerResult.removeObservers(this@AddConsignee)
    }
}