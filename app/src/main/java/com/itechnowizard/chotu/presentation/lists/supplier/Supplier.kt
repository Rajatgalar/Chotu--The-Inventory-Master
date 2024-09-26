package com.itechnowizard.chotu.presentation.lists.supplier

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivitySupplierBinding
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.ProgressBarUtil
import com.itechnowizard.chotu.utils.Resource
import com.itechnowizard.chotu.utils.ToolbarUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Supplier : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierBinding
    private val viewModel: SupplierViewModel by viewModels()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(
                toolbarLayout,
                true,
                Constants.TOOLBAR_FIRM_DETAILS,
                Constants.TOOLBAR_SAVE
            )

            etState.setAdapter(
                ArrayAdapter(
                    this@Supplier, R.layout.list_item,
                    R.id.item_title, resources.getStringArray(R.array.india_states_array)
                )
            )

            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }

            btnSave.setOnClickListener { saveData() }

            ivLogo.setOnClickListener { pickImage() }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }
        }
    }

    private fun saveData() {
        val message = checkValidation()
        if (message == Constants.OK) {
            viewModel.saveSupplierDetails(
                imageUri,
                SupplierModel(
                    address = binding.etAddress.text.toString(),
                    city = binding.etCity.text.toString(),
                    email = binding.etEmail.text.toString(),
                    firmName = binding.etFirmName.text.toString(),
                    gstin = binding.etGSTIN.text.toString(),
                    imageUrl = Constants.DOWNLOAD_URL,
                    mobile = binding.etMobile.text.toString(),
                    panCard = binding.etPanCard.text.toString(),
                    pincode = binding.etPincode.text.toString(),
                    state = binding.etState.text.toString()
                )
            )
        } else {
            Toast.makeText(this@Supplier, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadSupplierDetails()
        viewModel.supplyDetailsState.observe(this) { state ->
            if (state.isLoading) {
                ProgressBarUtil.showProgressBar(binding.progressBarLayout)
            } else if (state.error.isNotBlank()) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                Toast.makeText(this, "Error : ${state.error}", Toast.LENGTH_SHORT).show()
            } else if (state.data != null) {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                binding.etFirmName.setText(state.data.firmName)
                binding.etGSTIN.setText(state.data.gstin)
                binding.etPanCard.setText(state.data.panCard)
                binding.etEmail.setText(state.data.email)
                binding.etMobile.setText(state.data.mobile)
                binding.etAddress.setText(state.data.address)
                binding.etCity.setText(state.data.city)
                binding.etPincode.setText(state.data.pincode)

                if (!state.data.imageUrl.isNullOrEmpty()) {
                    Glide.with(this).load(state.data.imageUrl).into(binding.ivLogo)
                    Constants.DOWNLOAD_URL = state.data.imageUrl.toString()
                }
            } else {
                ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
            }
        }

        viewModel.addSupplierDetailsResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(this, "Supplier details added successfully", Toast.LENGTH_SHORT)
                        .show()
                    backToPreviousActivity()
                }
                is Resource.Error -> {
                    ProgressBarUtil.hideProgressBar(binding.progressBarLayout)
                    Toast.makeText(
                        this,
                        "Error adding supplier details: ${result.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is Resource.Loading -> {
                    ProgressBarUtil.showProgressBar(binding.progressBarLayout)
                }
            }
        }
    }

    private fun backToPreviousActivity() {
        val intent = Intent()
        intent.putExtra(
            Constants.INTENT_MODEL, SupplierModel(
                address = binding.etAddress.text.toString(),
                city = binding.etCity.text.toString(),
                email = binding.etEmail.text.toString(),
                firmName = binding.etFirmName.text.toString(),
                gstin = binding.etGSTIN.text.toString(),
                imageUrl = Constants.DOWNLOAD_URL,
                mobile = binding.etMobile.text.toString(),
                panCard = binding.etPanCard.text.toString(),
                pincode = binding.etPincode.text.toString(),
                state = binding.etState.text.toString()
            )
        )
        intent.putExtra(Constants.INTENT_ACTIVITY, Constants.INTENT_SUPPLIER_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()

    }

    override fun onStop() {
        super.onStop()
        viewModel.supplyDetailsState.removeObservers(this)
        viewModel.addSupplierDetailsResult.removeObservers(this)
    }

    private fun pickImage(){
        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        imageUri?.let {
            this.imageUri = imageUri
            binding.ivLogo.setImageURI(imageUri)
        }
    }

    private fun checkValidation(): String {
        if (binding.etFirmName.text.isNullOrEmpty())
            return "Enter Company Name"
        return Constants.OK
    }

}


//    private fun checkGalleryPermission() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.CAMERA
//            ) != PackageManager.PERMISSION_GRANTED ||
//            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
//                    ContextCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
//                    ) != PackageManager.PERMISSION_GRANTED) ||
//            (Build.VERSION.SDK_INT < Build.VERSION_CODES.R &&
//                    (ContextCompat.checkSelfPermission(
//                        this,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    ) != PackageManager.PERMISSION_GRANTED ||
//                            ContextCompat.checkSelfPermission(
//                                this,
//                                Manifest.permission.READ_EXTERNAL_STORAGE
//                            ) != PackageManager.PERMISSION_GRANTED))
//        ) {
//            // Request the permissions
//            ActivityCompat.requestPermissions(
//                this,
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                    arrayOf(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
//                    )
//                } else {
//                    arrayOf(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.READ_EXTERNAL_STORAGE
//                    )
//                },
//                Constants.PERMISSIONS_REQUEST_CAMERA_AND_STORAGE
//            )
//        } else {
//            openImageGalleryOrCamera()
//        }
//    }
//
//
//    private fun openImageGalleryOrCamera() {
//
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, Constants.REQUEST_SELECT_IMAGE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == RESULT_OK) {
//            when (requestCode) {
//                Constants.REQUEST_SELECT_IMAGE -> {
//                    val imageUri = data?.data
//                    if (imageUri != null) {
//                        this.imageUri = imageUri
//                        binding.ivLogo.setImageURI(imageUri)
//                    }
//                }
//            }
//        }
//    }




