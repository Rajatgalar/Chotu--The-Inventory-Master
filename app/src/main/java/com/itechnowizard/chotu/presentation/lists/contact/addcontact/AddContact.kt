package com.itechnowizard.chotu.presentation.lists.contact.addcontact

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityAddContactBinding
import com.itechnowizard.chotu.domain.model.ContactModel
import com.itechnowizard.chotu.presentation.lists.contact.ContactViewModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.Resource
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddContact : AppCompatActivity() {
    private lateinit var binding: ActivityAddContactBinding
    private val viewModel: ContactViewModel by viewModels()

    private var isForUpdate : Boolean = false
    private var DocumentId : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        binding = ActivityAddContactBinding.inflate(layoutInflater)
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

            viewModel.addContactResult.observe(this@AddContact) { result ->
                when (result) {
                    is Resource.Success -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(this@AddContact, "Contact details Saved successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    is Resource.Error -> {
                        ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                        Toast.makeText(
                            this@AddContact,
                            "Error adding Contact details: ${result.message}",
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
        ToolbarUtils.setToolbar(binding.toolbarLayout,true, Constants.ADD_CONTACT, Constants.TOOLBAR_SAVE)
    }

    private fun setDataForEdit() {

        val model = intent.getParcelableExtra<ContactModel>(Constants.INTENT_MODEL)!!
        DocumentId = intent.getStringExtra(Constants.DOCUMENT_ID)!!
        binding.etIFSCCode.setText(model.name)
        binding.etBankName.setText(model.email)
        binding.etBranchName.setText(model.phone)

        ToolbarUtils.setToolbar(binding.toolbarLayout,true, Constants.EDIT_CONTACT, Constants.TOOLBAR_UPDATE)
        binding.btnSave.text = Constants.TOOLBAR_UPDATE
    }

    private fun saveData() {
        val message = checkValidation()
        if (message == Constants.OK) {
            viewModel.addContact(
                ContactModel(
                phone = binding.etBranchName.text.toString(),
                name = binding.etIFSCCode.text.toString(),
                email = binding.etBankName.text.toString()
            ),isForUpdate,DocumentId
            )
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkValidation(): String {
        if (binding.etIFSCCode.text.isNullOrEmpty())
            return "Enter Name"
        return Constants.OK
    }

    override fun onStop() {
        super.onStop()
        viewModel.addContactResult.removeObservers(this)
    }

}