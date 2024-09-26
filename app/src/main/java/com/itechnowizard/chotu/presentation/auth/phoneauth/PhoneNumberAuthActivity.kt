package com.itechnowizard.chotu.presentation.auth.phoneauth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.itechnowizard.chotu.databinding.ActivityPhoneNumberAuthBinding
import com.itechnowizard.chotu.presentation.dashboard.DashboardActivity
import com.itechnowizard.chotu.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "PhoneNumberAuthActivity"

@AndroidEntryPoint
class PhoneNumberAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneNumberAuthBinding
    private val viewModel: PhoneNumberAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneNumberAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val phoneNumber = intent.getStringExtra(Constants.USER_MOBILE_NUMBER)

        viewModel.sendVerificationCode("+91$phoneNumber", this)

        binding.buttonVerify.setOnClickListener {
            val code = binding.etOTP.text.toString()
            viewModel.verifyVerificationCode(code)
        }

    }

    override fun onStart() {
        super.onStart()
        viewModel.isCodeSent.observe(this, Observer { isCodeSent ->
            if (isCodeSent) {
                Snackbar.make(binding.root, "Verification code sent", Snackbar.LENGTH_SHORT).show()
            }
        })

        viewModel.isVerificationSuccessful.observe(this, Observer { isSuccessful ->
            when (isSuccessful) {
                Constants.NEW_USER -> {
                    Toast.makeText(this, "Verification Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
//                    startActivity(Intent(this, DashboardActivity::class.java).putExtra(Constants.NEW_USER,true))
                }
                Constants.EXISTING_USER -> {
                    Toast.makeText(this, "Verification Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                Constants.FAILED -> {
                    Toast.makeText(this, "Verification Failed", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        viewModel.isCodeSent.removeObservers(this)
        viewModel.isVerificationSuccessful.removeObservers(this)
    }
}
