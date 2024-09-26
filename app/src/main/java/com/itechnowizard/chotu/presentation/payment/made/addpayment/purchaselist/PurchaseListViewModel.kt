package com.itechnowizard.chotu.presentation.payment.made.addpayment.purchaselist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.usecase.PaymentMadeUseCase
import com.itechnowizard.chotu.domain.usecase.PaymentReceiptUseCase
import com.itechnowizard.chotu.domain.usecase.TransportListUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PurchaseListViewModel @Inject constructor(
    private val purchaseListUseCase: PaymentMadeUseCase
) : ViewModel() {
    private val _purchaseListState = MutableLiveData<ListState<QuerySnapshot>>()
    val purchaseListState: LiveData<ListState<QuerySnapshot>> = _purchaseListState
    

    fun loadTransportList(sellerId : String) {
        purchaseListUseCase.fetchPurchaseList(sellerId).onEach { result->
            when(result){
                is Resource.Success ->{
                    _purchaseListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _purchaseListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _purchaseListState.value =ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }
}