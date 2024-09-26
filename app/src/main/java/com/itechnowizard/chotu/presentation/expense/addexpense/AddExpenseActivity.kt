package com.itechnowizard.chotu.presentation.expense.addexpense

import android.Manifest.permission.*
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityAddExpenseBinding
import com.itechnowizard.chotu.presentation.expense.ExpenseViewModel
import com.itechnowizard.chotu.utils.Constants
import com.itechnowizard.chotu.utils.Constants.PERMISSIONS_REQUEST_CAMERA_AND_STORAGE
import com.itechnowizard.chotu.utils.Constants.REQUEST_SELECT_IMAGE
import com.itechnowizard.chotu.utils.Constants.REQUEST_TAKE_PICTURE
import com.itechnowizard.chotu.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private val viewModel: ExpenseViewModel by viewModels()
    private var ImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            etPaymentMode.setAdapter(ArrayAdapter(this@AddExpenseActivity, R.layout.list_item,
                R.id.item_title,resources.getStringArray(R.array.PaymentMode)))

            etCategory.setAdapter(ArrayAdapter(this@AddExpenseActivity, R.layout.list_item,
                R.id.item_title,resources.getStringArray(R.array.ExpenseCategory)
                ))

            etPaymentMode.setText(resources.getStringArray(R.array.PaymentMode)[0], false)
            etCategory.setText(resources.getStringArray(R.array.ExpenseCategory)[0], false)

            etDateOfExpense.setText(DateUtils.getTodayDate())

            etDateOfExpense.setOnClickListener { openDatePicker() }

            btnImage.setOnClickListener {
                pickImage()
            }

            btnSave.setOnClickListener {
                val message = checkValidation()
                if (message == Constants.OK) {
                    if (ImageUri == null) {
                        viewModel.saveExpenseToFirebase(
                            accountType = etAccountType.text.toString(),
                            amount = etAmount.text.toString(),
                            category = etCategory.text.toString(),
                            dateOfExpense = etDateOfExpense.text.toString(),
                            imageUrl = "",
                            note = etNote.text.toString(),
                            paymentMode = etPaymentMode.text.toString(),
                            spendFor = etSpendfor.text.toString()
                        )
                    }else{
                        viewModel.saveExpenseToFirebaseWithImage(
                            imageUri = ImageUri!!,
                            accountType = etAccountType.text.toString(),
                            amount = etAmount.text.toString(),
                            category = etCategory.text.toString(),
                            dateOfExpense = etDateOfExpense.text.toString(),
                            imageUrl = "",
                            note = etNote.text.toString(),
                            paymentMode = etPaymentMode.text.toString(),
                            spendFor = etSpendfor.text.toString()
                        )
                    }
                    Toast.makeText(this@AddExpenseActivity, "Expense Added", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this@AddExpenseActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun pickImage(){
        singlePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val singlePhotoPickerLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { imageUri: Uri? ->
        imageUri?.let {
            this.ImageUri = imageUri
            binding.btnImage.setImageURI(imageUri)
        }
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this@AddExpenseActivity,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                binding.etDateOfExpense.setText("$dayOfMonth-${month + 1}-$year")
            }, year, month, day
        )

        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    private fun checkValidation(): String {
        if (binding.etSpendfor.text.isNullOrEmpty())
            return "Please Add Expense Name"
        if (binding.etAmount.text.isNullOrEmpty())
            return "Please enter the Amount"
        if (binding.etCategory.text.toString() == resources.getStringArray(R.array.ExpenseCategory)[0])
            return "Please select a Category"
        return Constants.OK
    }

}