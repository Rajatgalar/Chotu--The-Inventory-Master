package com.itechnowizard.chotu.presentation.payment.receipt

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.PaymentReceiptModel
import com.itechnowizard.chotu.domain.usecase.PaymentReceiptUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PaymentReceiptViewModel @Inject constructor(
    private val receiptUseCase: PaymentReceiptUseCase
) : ViewModel() {
    private val _addPaymentReceiptResult = MutableLiveData<Resource<Unit>>()
    val addPaymentReceiptResult: LiveData<Resource<Unit>> = _addPaymentReceiptResult

    private val _removePaymentReceiptResult = MutableLiveData<Resource<Unit>>()
    val removePaymentReceiptResult: LiveData<Resource<Unit>> = _removePaymentReceiptResult

    private val _receiptListState = MutableLiveData<ListState<QuerySnapshot>>()
    val receiptListState: LiveData<ListState<QuerySnapshot>> = _receiptListState

    fun addPaymentReceipt(
        receiptModel: PaymentReceiptModel,
        isForUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double,
    ) {
        val result = receiptUseCase.sendData(receiptModel,isForUpdate,documentId,previousBillFinalAmount)
        _addPaymentReceiptResult.value = result
    }

    fun deletePaymentReceiptDocument(docId: String, buyerId: String){
        val result = receiptUseCase.deleteDocument(docId,buyerId)
        _removePaymentReceiptResult.value = result
    }

    fun loadPaymentReceiptList() {
        receiptUseCase.fetchPaymentReceiptList().onEach { result->
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