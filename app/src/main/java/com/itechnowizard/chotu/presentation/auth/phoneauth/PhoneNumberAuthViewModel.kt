package com.itechnowizard.chotu.presentation.auth.phoneauth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.itechnowizard.chotu.domain.usecase.AddNameUseCase
import com.itechnowizard.chotu.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "PhoneNumberAuthViewMode"
@HiltViewModel
class PhoneNumberAuthViewModel @Inject constructor(
    private val addNewUserToFirebase: AddNameUseCase
) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var verificationId: String

    private val _isCodeSent = MutableLiveData<Boolean>()
    val isCodeSent: LiveData<Boolean>
        get() = _isCodeSent

    private val _isVerificationSuccessful = MutableLiveData<String>()
    val isVerificationSuccessful: LiveData<String>
        get() = _isVerificationSuccessful

    fun sendVerificationCode(phoneNumber: String,activity : Activity) {
        verifyPhoneNumberCallBack()
        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L,TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private lateinit var callback : PhoneAuthProvider.OnVerificationStateChangedCallbacks

    private fun  verifyPhoneNumberCallBack(){
        callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                signInWithCredential(p0)
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                _isVerificationSuccessful.value = Constants.FAILED
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, token)
                _isCodeSent.value = true
                this@PhoneNumberAuthViewModel.verificationId = verificationId
            }

        }
    }

    fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    if(task.result.additionalUserInfo!!.isNewUser) {
                        addNewUserToFirebase.execute(task.result.user!!.uid)
                        _isVerificationSuccessful.value = Constants.NEW_USER
                    } else {
                        _isVerificationSuccessful.value = Constants.EXISTING_USER
                    }
                } else {
                    _isVerificationSuccessful.value = Constants.FAILED
                }
            }
    }

/*
    fun verifyVerificationCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                Log.d(TAG, "verifyVerificationCode: success ")
                if(task.result.additionalUserInfo!!.isNewUser) {
                    Log.d(TAG, "verifyVerificationCode: User is new")
                 //   addNewUserToFirebase.execute(task.result.user!!.uid)
                    _isVerificationSuccessful.value = Constants.NEW_USER
                }
                else {
                    Log.d(TAG, "verifyVerificationCode: User is existing")
                    _isVerificationSuccessful.value = Constants.EXISTING_USER
                }

            }
            .addOnFailureListener { failure ->
                Log.d(TAG, "verifyVerificationCode: failure:  $failure")
                _isVerificationSuccessful.value = Constants.FAILED
            }
    }


 */
}

/*
//Working code

PhoneAuthProvider.getInstance().verifyPhoneNumber(
    phoneNumber,
    60,
    java.util.concurrent.TimeUnit.SECONDS,
   activity,
    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: successful login")
            if(credential.smsCode !=null)
                verifyVerificationCode(credential.smsCode!!)
//                    _isVerificationSuccessful.value = true
        }

        override fun onVerificationFailed(p0: FirebaseException) {
            Log.d(TAG, "onVerificationFailed: "+p0.message.toString())
//                    _isVerificationSuccessful.value = false
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@PhoneNumberAuthViewModel.verificationId = verificationId
            _isCodeSent.value = true
        }
    }
)

 */