package com.itechnowizard.chotu.presentation.purchase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.PurchaseModel
import com.itechnowizard.chotu.domain.usecase.PurchaseUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel  @Inject constructor(
    private val purchaseUseCase: PurchaseUseCase
) : ViewModel()  {
    private val _addPurchaseResult = MutableLiveData<Resource<Unit>>()
    val addPurchaseResult: LiveData<Resource<Unit>> = _addPurchaseResult

    private val _removePurchaseResult = MutableLiveData<Resource<Unit>>()
    val removePurchaseResult: LiveData<Resource<Unit>> = _removePurchaseResult

    private val _purchaseListState = MutableLiveData<ListState<QuerySnapshot>>()
    val purchaseListState: LiveData<ListState<QuerySnapshot>> = _purchaseListState

    fun addPurchase(
        purchaseModel: PurchaseModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    ) {
        val result = purchaseUseCase.sendData(purchaseModel,isForUpdate,documentId,bitmapByteData,
            previousBillFinalAmount)
        _addPurchaseResult.value = result
    }

    fun deletePurchaseDocument(docId: String, sellerId: String){
        val result = purchaseUseCase.deleteDocument(docId, sellerId)
        _removePurchaseResult.value = result
    }

    fun loadPurchaseList() {
        purchaseUseCase.fetchInvoiceList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _purchaseListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _purchaseListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _purchaseListState.value = ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

}