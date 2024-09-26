package com.itechnowizard.chotu.presentation.quotation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.QuotationModel
import com.itechnowizard.chotu.domain.usecase.QuotationUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class QuotationViewModel @Inject constructor(
    private val quotationUseCase: QuotationUseCase
) : ViewModel() {
    private val _addQuotationResult = MutableLiveData<Resource<Unit>>()
    val addQuotationResult: LiveData<Resource<Unit>> = _addQuotationResult

    private val _removeQuotationResult = MutableLiveData<Resource<Unit>>()
    val removeQuotationResult: LiveData<Resource<Unit>> = _removeQuotationResult

    private val _quotationListState = MutableLiveData<ListState<QuerySnapshot>>()
    val quotationListState: LiveData<ListState<QuerySnapshot>> = _quotationListState

    fun addQuotation(
        quotationModel: QuotationModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?
    ) {
        val result = quotationUseCase.sendData(quotationModel,isForUpdate,documentId,bitmapByteData)
        _addQuotationResult.value = result
    }

    fun deleteQuotationDocument(docId: String){
        val result = quotationUseCase.deleteDocument(docId)
        _removeQuotationResult.value = result
    }

    fun loadQuotationList() {
        quotationUseCase.fetchQuotationList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _quotationListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _quotationListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _quotationListState.value = ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }



}