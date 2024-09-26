package com.itechnowizard.chotu.presentation.proforma

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.usecase.InvoiceUseCase
import com.itechnowizard.chotu.domain.usecase.ProformaInvoiceUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProformaInvoiceViewModel @Inject constructor(
    private val invoiceUseCase: ProformaInvoiceUseCase
) : ViewModel() {
    private val _addInvoiceResult = MutableLiveData<Resource<Unit>>()
    val addInvoiceResult: LiveData<Resource<Unit>> = _addInvoiceResult

    private val _removeInvoiceResult = MutableLiveData<Resource<Unit>>()
    val removeInvoiceResult: LiveData<Resource<Unit>> = _removeInvoiceResult

    private val _invoiceListState = MutableLiveData<ListState<QuerySnapshot>>()
    val invoiceListState: LiveData<ListState<QuerySnapshot>> = _invoiceListState

    fun addInvoice(
        invoiceModel: InvoiceModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double,
    ) {
        val result = invoiceUseCase.sendData(invoiceModel,isForUpdate,documentId,bitmapByteData,previousBillFinalAmount)
        _addInvoiceResult.value = result
    }

    fun deleteInvoiceDocument(docId: String){
        val result = invoiceUseCase.deleteDocument(docId)
        _removeInvoiceResult.value = result
    }

    fun loadInvoiceList() {
        invoiceUseCase.fetchInvoiceList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _invoiceListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _invoiceListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _invoiceListState.value = ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }



}