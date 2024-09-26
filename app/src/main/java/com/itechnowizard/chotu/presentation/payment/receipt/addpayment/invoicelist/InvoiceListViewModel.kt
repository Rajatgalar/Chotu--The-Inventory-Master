package com.itechnowizard.chotu.presentation.payment.receipt.addpayment.invoicelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.usecase.PaymentReceiptUseCase
import com.itechnowizard.chotu.domain.usecase.TransportListUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class InvoiceListViewModel @Inject constructor(
    private val invoiceListUseCase: PaymentReceiptUseCase
) : ViewModel() {
    private val _invoiceListState = MutableLiveData<ListState<QuerySnapshot>>()
    val invoiceListState: LiveData<ListState<QuerySnapshot>> = _invoiceListState
    

    fun loadTransportList(buyerId : String) {
        invoiceListUseCase.fetchInvoiceList(buyerId).onEach { result->
            when(result){
                is Resource.Success ->{
                    _invoiceListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _invoiceListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _invoiceListState.value =ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }
}