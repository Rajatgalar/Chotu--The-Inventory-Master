package com.itechnowizard.chotu.presentation.lists.seller.addSeller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityAddSellerBinding
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.presentation.lists.seller.SellerViewModel

import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.Resource
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSeller : AppCompatActivity() {

    private lateinit var binding: ActivityAddSellerBinding
    private val viewModel: SellerViewModel by viewModels()

    private var isForUpdate : Boolean = false
    private var docId : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSellerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isForUpdate = intent.getBooleanExtra(Constants.IS_INTENT_HAVING_DATA,false)
        if(isForUpdate){
            setDataForEdit()
        }else
            setDataForAdd()

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)

            etState.setAdapter(
                ArrayAdapter(this@AddSeller, R.layout.list_item,R.id.item_title,
                    resources.getStringArray(R.array.india_states_array))
            )
            etGstTreatment.setAdapter(
                ArrayAdapter(this@AddSeller, R.layout.list_item,R.id.item_title,
                    resources.getStringArray(R.array.gst_treatment_type))
            )

            toolbarLayout.toolbarBack.setOnClickListener { finish() }
            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }
            btnSave.setOnClickListener { saveData() }

            viewModel.addSellerResult.observe(this@AddSeller) { result ->
                when (result) {
                    is Resource.Success -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(this@AddSeller, "Seller details Saved successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is Resource.Error -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(
                            this@AddSeller,
                            "Error adding Seller details: ${result.message}",
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
        ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_ADD_SELLER,Constants.TOOLBAR_SAVE)
    }

    private fun setDataForEdit() {

        val model = intent.getParcelableExtra<SellerModel>(Constants.INTENT_MODEL)!!
        docId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        binding.etCompanyName.setText(model.companyName)
        binding.etGSTIN.setText(model.gstin)
        binding.etEmail.setText(model.email)
        binding.etMobile.setText(model.mobile)
        binding.etAddress.setText(model.address)
        binding.etCity.setText(model.city)
        binding.etState.setText(model.state)
        binding.etGstTreatment.setText(model.gsttreatmentType)

        ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_Edit_SELLER,Constants.TOOLBAR_UPDATE)
        binding.btnSave.text = Constants.TOOLBAR_UPDATE

    }

    private fun saveData() {
        val message = checkValidation()
        if (message == Constants.OK) {
            viewModel.addSeller(SellerModel(
                gstin = binding.etGSTIN.text.toString(),
                companyName = binding.etCompanyName.text.toString(),
                email = binding.etEmail.text.toString(),
                mobile = binding.etMobile.text.toString(),
                address = binding.etAddress.text.toString(),
                city = binding.etCity.text.toString(),
                state  = binding.etState.text.toString(),
                gsttreatmentType = binding.etGstTreatment.text.toString()
            ),isForUpdate,docId
            )
        } else {
            Toast.makeText(this@AddSeller, message, Toast.LENGTH_SHORT).show()
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
        viewModel.addSellerResult.removeObservers(this@AddSeller)
    }
}