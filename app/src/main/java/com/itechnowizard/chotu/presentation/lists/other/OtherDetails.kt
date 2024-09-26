package com.itechnowizard.chotu.presentation.lists.other

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.itechnowizard.chotu.R
import com.itechnowizard.chotu.databinding.ActivityOtherDetailsBinding
import com.itechnowizard.chotu.domain.model.OtherDetailModel
import com.itechnowizard.chotu.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_expense.*
import kotlinx.android.synthetic.main.activity_other_details.*

@AndroidEntryPoint
class OtherDetails : AppCompatActivity() {

    private lateinit var binding: ActivityOtherDetailsBinding
    private var otherDetails = OtherDetailModel()
    private var isGstTextWatcher = true  //when this is true we are watching the GST tv with textwatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        otherDetails = intent.getParcelableExtra<OtherDetailModel>(Constants.INTENT_DATA)!!
        setData()

        binding.apply {
            setSupportActionBar(toolbarLayout.toolbar)
            ToolbarUtils.setToolbar(
                toolbarLayout,
                true,
                Constants.TOOLBAR_OTHER_DETAILS,
                Constants.TOOLBAR_SAVE
            )

            toolbarLayout.toolbarMenuText.setOnClickListener { saveData() }

            toolbarLayout.toolbarBack.setOnClickListener { finish() }

            etTcsType.setAdapter(
                ArrayAdapter(
                    this@OtherDetails,
                    android.R.layout.simple_spinner_item,
                    resources.getStringArray(R.array.OtherDetailsTCS)
                )
            )
            etTcsType.setText(resources.getStringArray(R.array.OtherDetailsTCS)[0])

            radioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.rb1 ->{
                        setViewProperties(etFreightCharge,etFreightChargeGst,tvFreightChargeAmount,1)
                        setViewProperties(etInsuranceCharge,etInsuranceChargeGst,tvInsuranceChargeAmount,1)
                        setViewProperties(etLoadingCharge,etLoadingChargeGst,tvLoadingChargeAmount,1)
                        setViewProperties(etPackagingCharge,etPackagingChargeGst,tvPackagingChargeAmount,1)
                        setViewProperties(etOtherCharge,etOtherChargeGst,tvOtherChargeAmount,1)
                    }
                    R.id.rb2 -> {
                        setViewProperties(etFreightCharge,etFreightChargeGst,tvFreightChargeAmount,2)
                        setViewProperties(etInsuranceCharge,etInsuranceChargeGst,tvInsuranceChargeAmount,2)
                        setViewProperties(etLoadingCharge,etLoadingChargeGst,tvLoadingChargeAmount,2)
                        setViewProperties(etPackagingCharge,etPackagingChargeGst,tvPackagingChargeAmount,2)
                        setViewProperties(etOtherCharge,etOtherChargeGst,tvOtherChargeAmount,2)
                    }
                    R.id.rb3 ->{
                        setViewProperties(etFreightCharge,etFreightChargeGst,tvFreightChargeAmount,3)
                        setViewProperties(etInsuranceCharge,etInsuranceChargeGst,tvInsuranceChargeAmount,3)
                        setViewProperties(etLoadingCharge,etLoadingChargeGst,tvLoadingChargeAmount,3)
                        setViewProperties(etPackagingCharge,etPackagingChargeGst,tvPackagingChargeAmount,3)
                        setViewProperties(etOtherCharge,etOtherChargeGst,tvOtherChargeAmount,3)
                    }
                }

            }

            etFreightCharge.addTextChangedListener(textWatcherForFreightAmount)
            etFreightChargeGst.addTextChangedListener(textWatcherForFreightAmount)
            tvFreightChargeAmount.addTextChangedListener(textWatcherForFreightAmount)
            etInsuranceCharge.addTextChangedListener(textWatcherForInsuranceAmount)
            etInsuranceChargeGst.addTextChangedListener(textWatcherForInsuranceAmount)
            tvInsuranceChargeAmount.addTextChangedListener(textWatcherForInsuranceAmount)
            etLoadingCharge.addTextChangedListener(textWatcherForLoadingAmount)
            etLoadingChargeGst.addTextChangedListener(textWatcherForLoadingAmount)
            tvLoadingChargeAmount.addTextChangedListener(textWatcherForLoadingAmount)
            etPackagingCharge.addTextChangedListener(textWatcherForPackageAmount)
            etPackagingChargeGst.addTextChangedListener(textWatcherForPackageAmount)
            tvPackagingChargeAmount.addTextChangedListener(textWatcherForPackageAmount)
            etOtherCharge.addTextChangedListener(textWatcherForOtherAmount)
            etOtherChargeGst.addTextChangedListener(textWatcherForOtherAmount)
            tvOtherChargeAmount.addTextChangedListener(textWatcherForOtherAmount)
        }
    }

    private val textWatcherForFreightAmount = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if(isGstTextWatcher){
                tvFreightChargeAmount.removeTextChangedListener(this)
                tvFreightChargeAmount.setText(String.format("%.2f", calculateAmount(etFreightCharge.text.toString().toDoubleOrNull() ?: 0.0,etFreightChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                tvFreightChargeAmount.addTextChangedListener(this)
            }else{
                etFreightCharge.removeTextChangedListener(this)
                etFreightCharge.setText(String.format("%.2f", calculateTaxableAmount(tvFreightChargeAmount.text.toString().toDoubleOrNull() ?: 0.0,etFreightChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                etFreightCharge.addTextChangedListener(this)
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val textWatcherForInsuranceAmount = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if(isGstTextWatcher){
                tvInsuranceChargeAmount.removeTextChangedListener(this)
                tvInsuranceChargeAmount.setText(String.format("%.2f", calculateAmount(etInsuranceCharge.text.toString().toDoubleOrNull() ?: 0.0,etInsuranceChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                tvInsuranceChargeAmount.addTextChangedListener(this)
            }else{
                etInsuranceCharge.removeTextChangedListener(this)
                etInsuranceCharge.setText(String.format("%.2f", calculateTaxableAmount(tvInsuranceChargeAmount.text.toString().toDoubleOrNull() ?: 0.0,etInsuranceChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                etInsuranceCharge.addTextChangedListener(this)
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private val textWatcherForLoadingAmount = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if(isGstTextWatcher){
                tvLoadingChargeAmount.removeTextChangedListener(this)
                tvLoadingChargeAmount.setText(String.format("%.2f", calculateAmount(etLoadingCharge.text.toString().toDoubleOrNull() ?: 0.0,etLoadingChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                tvLoadingChargeAmount.addTextChangedListener(this)
            }else{
                etLoadingCharge.removeTextChangedListener(this)
                etLoadingCharge.setText(String.format("%.2f", calculateTaxableAmount(tvLoadingChargeAmount.text.toString().toDoubleOrNull() ?: 0.0,etLoadingChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                etLoadingCharge.addTextChangedListener(this)
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private val textWatcherForPackageAmount = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if(isGstTextWatcher){
                tvPackagingChargeAmount.removeTextChangedListener(this)
                tvPackagingChargeAmount.setText(String.format("%.2f", calculateAmount(etPackagingCharge.text.toString().toDoubleOrNull() ?: 0.0,etPackagingChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                tvPackagingChargeAmount.addTextChangedListener(this)
            }else{
                etPackagingCharge.removeTextChangedListener(this)
                etPackagingCharge.setText(String.format("%.2f", calculateTaxableAmount(tvPackagingChargeAmount.text.toString().toDoubleOrNull() ?: 0.0,etPackagingChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                etPackagingCharge.addTextChangedListener(this)
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }
    private val textWatcherForOtherAmount = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if(isGstTextWatcher){
                tvOtherChargeAmount.removeTextChangedListener(this)
                tvOtherChargeAmount.setText(String.format("%.2f", calculateAmount(etOtherCharge.text.toString().toDoubleOrNull() ?: 0.0,etOtherChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                tvOtherChargeAmount.addTextChangedListener(this)
            }else{
                etOtherCharge.removeTextChangedListener(this)
                etOtherCharge.setText(String.format("%.2f", calculateTaxableAmount(tvOtherChargeAmount.text.toString().toDoubleOrNull() ?: 0.0,etOtherChargeGst.text.toString().toDoubleOrNull() ?: 0.0)))
                etOtherCharge.addTextChangedListener(this)
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private fun calculateAmount(charge: Double, gst: Double): Double {
        return charge + (charge * gst/100)
    }

    private fun calculateTaxableAmount(amount: Double, gst: Double): Double {
        return amount / (1 + gst/100)
    }

    fun setViewProperties(charge: EditText, gst: EditText, amount : EditText, isEnabled: Int) {
        when(isEnabled){
            1 -> {
                amount.apply {
                    this.isEnabled = false
                    setText("")
                    setBackgroundColor(ContextCompat.getColor(context, R.color.color_grey_background))
                }
                charge.apply {
                    this.isEnabled = true
                    setText("")
                    setBackgroundResource(R.drawable.round_border_grey)
                }
                gst.apply {
                    this.isEnabled = true
                    isGstTextWatcher = true
                    setText("")
                    setBackgroundResource(R.drawable.round_border_grey)
                }
            }
            2 -> {
                charge.apply {
                    this.isEnabled = false
                    setText("")
                    setBackgroundColor(ContextCompat.getColor(context, R.color.color_grey_background))
                }
                amount.apply {
                    this.isEnabled = true
                    setText("")
                    setBackgroundResource(R.drawable.round_border_grey)
                }
                gst.apply {
                    this.isEnabled = true
                    isGstTextWatcher = false
                    setText("")
                    setBackgroundResource(R.drawable.round_border_grey)
                }
            }
             3->{
                 amount.apply {
                     this.isEnabled = false
                     setText("")
                     setBackgroundColor(ContextCompat.getColor(context, R.color.color_grey_background))
                 }
                 charge.apply {
                     this.isEnabled = true
                     setText("")
                     setBackgroundResource(R.drawable.round_border_grey)
                 }
                 gst.apply {
                     this.isEnabled = false
                     isGstTextWatcher = true
                     setText("")
                     setBackgroundColor(ContextCompat.getColor(context, R.color.color_grey_background))
                 }
             }
        }
    }

    private fun setData() {
        binding.apply {
            binding.etChallanNumber.setText(otherDetails.challanNumber)
            binding.etEWayBillNumber.setText(otherDetails.ewayBillNumber)
            binding.etFreightCharge.setText(otherDetails.freightCharge)
            binding.tvFreightChargeAmount.setText(otherDetails.freightChargeAmount)
            binding.etFreightChargeGst.setText(otherDetails.freightChargeGst)
            binding.etInsuranceCharge.setText(otherDetails.insuranceCharge)
            binding.tvInsuranceChargeAmount.setText(otherDetails.insuranceChargeAmount)
            binding.etInsuranceChargeGst.setText(otherDetails.insuranceChargeGst)
            binding.etLoadingCharge.setText(otherDetails.loadingCharge)
            binding.tvLoadingChargeAmount.setText(otherDetails.loadingChargeAmount)
            binding.etLoadingChargeGst.setText(otherDetails.loadingChargeGst)
            binding.etOtherCharge.setText(otherDetails.otherCharge)
            binding.tvOtherChargeAmount.setText(otherDetails.otherChargeAmount)
            binding.etOtherChargeGst.setText(otherDetails.otherChargeGst)
            binding.etOtherChargeName.setText(otherDetails.otherChargeName)
            binding.etPackagingCharge.setText(otherDetails.packagingCharge)
            binding.tvPackagingChargeAmount.setText(otherDetails.packagingChargeAmount)
            binding.etPackagingChargeGst.setText(otherDetails.packagingChargeGst)
            binding.etPODate.setText(otherDetails.podate)
            binding.etPONumber.setText(otherDetails.ponumber)
            binding.checkbox1.isChecked = otherDetails.reverseCharge.equals("Yes")
            binding.etSalesPerson.setText(otherDetails.salesPerson)
            when (otherDetails.taxPref) {
                "Radio button 1 is checked" -> binding.rb1.isChecked= true
                "Radio button 2 is checked" -> binding.rb2.isChecked = true
                "Radio button 3 is checked" -> binding.rb3.isChecked = true
            }
            binding.etTcs.setText(otherDetails.tcs)
            binding.etTcsType.setText(otherDetails.tcsType)
        }
    }

    fun getModel() = OtherDetailModel(
            challanNumber = binding.etChallanNumber.text.toString(),
            ewayBillNumber = binding.etEWayBillNumber.text.toString(),
            freightCharge = binding.etFreightCharge.text.toString(),
            freightChargeAmount = binding.tvFreightChargeAmount.text.toString(),
            freightChargeGst = binding.etFreightChargeGst.text.toString(),
            insuranceCharge = binding.etInsuranceCharge.text.toString(),
            insuranceChargeAmount = binding.tvInsuranceChargeAmount.text.toString(),
            insuranceChargeGst = binding.etInsuranceChargeGst.text.toString(),
            loadingCharge = binding.etLoadingCharge.text.toString(),
            loadingChargeAmount = binding.tvLoadingChargeAmount.text.toString(),
            loadingChargeGst = binding.etLoadingChargeGst.text.toString(),
            otherCharge = binding.etOtherCharge.text.toString(),
            otherChargeAmount = binding.tvOtherChargeAmount.text.toString(),
            otherChargeGst = binding.etOtherChargeGst.text.toString(),
            otherChargeName = binding.etOtherChargeName.text.toString().ifEmpty { "Other Charge" },
            packagingCharge = binding.etPackagingCharge.text.toString(),
            packagingChargeAmount = binding.tvPackagingChargeAmount.text.toString(),
            packagingChargeGst = binding.etPackagingChargeGst.text.toString(),
            podate = binding.etPODate.text.toString(),
            ponumber = binding.etPONumber.text.toString(),
            reverseCharge = if(binding.checkbox1.isChecked) "Yes" else "No" ,
            salesPerson = binding.etSalesPerson.text.toString(),
            taxPref = when{
                binding.rb1.isChecked -> "Radio button 1 is checked"
                binding.rb2.isChecked -> "Radio button 2 is checked"
                binding.rb3.isChecked -> "Radio button 3 is checked"
                else -> "No radio button is checked"
            },
            tcs = binding.etTcs.text.toString(),
            tcsType = binding.etTcsType.text.toString(),
        )

    private fun saveData() {
        backToPreviousActivity()
    }

    private fun backToPreviousActivity() {
        val intent = Intent()
        intent.putExtra(Constants.INTENT_MODEL, getModel())
        intent.putExtra(Constants.INTENT_ACTIVITY,Constants.INTENT_OTHER_ACTIVITY)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}