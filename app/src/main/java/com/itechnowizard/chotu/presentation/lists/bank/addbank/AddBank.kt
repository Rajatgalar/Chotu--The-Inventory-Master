package com.itechnowizard.chotu.presentation.lists.bank.addbank

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityAddBankBinding
import com.itechnowizard.chotu.domain.model.BankModel
import com.itechnowizard.chotu.presentation.lists.bank.BankViewModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.Resource
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddBank : AppCompatActivity() {

    private lateinit var binding: ActivityAddBankBinding
    private val viewModel: BankViewModel by viewModels()

    private var isForUpdate : Boolean = false
    private var DocumentId : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_bank)

        binding = ActivityAddBankBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isForUpdate = intent.getBooleanExtra(Constants.IS_INTENT_HAVING_DATA,false)
        if(isForUpdate){
            setDataForEdit()
        }else
            setDataForAdd()

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)

            toolbarLayout.toolbarBack.setOnClickListener { finish() }
            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }
            btnSave.setOnClickListener { saveData() }

            viewModel.addBankResult.observe(this@AddBank) { result ->
                when (result) {
                    is Resource.Success -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(this@AddBank, "Bank details Saved successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is Resource.Error -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(
                            this@AddBank,
                            "Error adding Bank details: ${result.message}",
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
        ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_ADD_BANK,Constants.TOOLBAR_SAVE)
    }

    private fun setDataForEdit() {

        val model = intent.getParcelableExtra<BankModel>(Constants.INTENT_MODEL)!!
        DocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        binding.etAccountNumber.setText(model.accountNumber)
        binding.etAccountHolderName.setText(model.accountHolderName)
        binding.etBankName.setText(model.bankName)
        binding.etBranchName.setText(model.branchName)
        binding.etIBANNumber.setText(model.ibanNumber)
        binding.etIFSCCode.setText(model.ifsccode)
        binding.etSwiftCode.setText(model.swiftCode)

        ToolbarUtils.setToolbar(binding.toolbarLayout,true,Constants.TOOLBAR_Edit_BANK,Constants.TOOLBAR_UPDATE)
        binding.btnSave.text = Constants.TOOLBAR_UPDATE
    }

    private fun saveData() {
        val message = checkValidation()
        if (message == Constants.OK) {
            viewModel.addBank(BankModel(
                accountNumber = binding.etAccountNumber.text.toString(),
                accountHolderName = binding.etAccountHolderName.text.toString(),
                bankName = binding.etBankName.text.toString(),
                branchName = binding.etBranchName.text.toString(),
                ibanNumber = binding.etIBANNumber.text.toString(),
                ifsccode = binding.etIFSCCode.text.toString(),
                swiftCode  = binding.etSwiftCode.text.toString(),
                 ),isForUpdate,DocumentId
            )
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkValidation(): String {
        if (binding.etIFSCCode.text.isNullOrEmpty())
            return "Enter IFSC Code"
        if (binding.etBankName.text.isNullOrEmpty())
            return "Enter Bank Name"
        if (binding.etBranchName.text.isNullOrEmpty())
            return "Enter Branch Name"
        if (binding.etAccountNumber.text.isNullOrEmpty())
            return "Enter Account Number"
        return Constants.OK
    }

    override fun onStop() {
        super.onStop()
        viewModel.addBankResult.removeObservers(this)
    }

}