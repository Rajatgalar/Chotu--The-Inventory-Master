package com.itechnowizard.chotu.presentation.payment.made

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.PaymentMadeModel
import com.itechnowizard.chotu.domain.usecase.PaymentMadeUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PaymentMadeViewModel @Inject constructor(
    private val receiptUseCase: PaymentMadeUseCase
) : ViewModel() {
    private val _addPaymentMadeResult = MutableLiveData<Resource<Unit>>()
    val addPaymentMadeResult: LiveData<Resource<Unit>> = _addPaymentMadeResult

    private val _removePaymentMadeResult = MutableLiveData<Resource<Unit>>()
    val removePaymentMadeResult: LiveData<Resource<Unit>> = _removePaymentMadeResult

    private val _receiptListState = MutableLiveData<ListState<QuerySnapshot>>()
    val receiptListState: LiveData<ListState<QuerySnapshot>> = _receiptListState

    fun addPaymentMade(
        receiptModel: PaymentMadeModel,
        isForUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double,
    ) {
        val result = receiptUseCase.sendData(receiptModel,isForUpdate,documentId,previousBillFinalAmount)
        _addPaymentMadeResult.value = result
    }

    fun deletePaymentMadeDocument(docId: String, buyerId: String){
        val result = receiptUseCase.deleteDocument(docId,buyerId)
        _removePaymentMadeResult.value = result
    }

    fun loadPaymentMadeList() {
        receiptUseCase.fetchPaymentMadeList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _receiptListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _receiptListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _receiptListState.value = ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }



}