package com.itechnowizard.chotu.presentation.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.itechnowizard.chotu.databinding.ActivityLoginBinding
import com.itechnowizard.chotu.presentation.auth.phoneauth.PhoneNumberAuthActivity
import com.itechnowizard.chotu.presentation.dashboard.DashboardActivity
import com.itechnowizard.chotu.utils.Constants

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if(FirebaseAuth.getInstance().currentUser !=null){
//         startActivity(Intent(this,DashboardActivity::class.java))
//         finish()
//        }

        binding.apply {
            btnLogin.setOnClickListener {
                if(etMobileNumber.text.isNullOrEmpty()){
                    Toast.makeText(this@LoginActivity,"Incomplete info",Toast.LENGTH_SHORT).show()
                }else if(etMobileNumber.text!!.length != 10){
                    Toast.makeText(this@LoginActivity,"Invalid Mobile number",Toast.LENGTH_SHORT).show()
                }else{
                    startActivity(Intent(this@LoginActivity,PhoneNumberAuthActivity::class.java)
                        .putExtra(Constants.USER_MOBILE_NUMBER,etMobileNumber.text.toString()))
                }
            }
        }
    }
}