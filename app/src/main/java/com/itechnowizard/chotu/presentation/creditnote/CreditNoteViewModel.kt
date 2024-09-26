package com.itechnowizard.chotu.presentation.creditnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.CreditNoteModel
import com.itechnowizard.chotu.domain.usecase.CreditNoteUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CreditNoteViewModel @Inject constructor(
    private val creditNoteUseCase: CreditNoteUseCase
) : ViewModel() {
    private val _addCreditNoteResult = MutableLiveData<Resource<Unit>>()
    val addCreditNoteResult: LiveData<Resource<Unit>> = _addCreditNoteResult

    private val _removeCreditNoteResult = MutableLiveData<Resource<Unit>>()
    val removeCreditNoteResult: LiveData<Resource<Unit>> = _removeCreditNoteResult

    private val _creditNoteListState = MutableLiveData<ListState<QuerySnapshot>>()
    val creditNoteListState: LiveData<ListState<QuerySnapshot>> = _creditNoteListState

    fun addCreditNote(
        creditNoteModel: CreditNoteModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double,
    ) {
        val result = creditNoteUseCase.sendData(creditNoteModel,isForUpdate,documentId,bitmapByteData, previousBillFinalAmount)
        _addCreditNoteResult.value = result
    }

    fun deleteCreditNoteDocument(docId: String, buyerId: String){
        val result = creditNoteUseCase.deleteDocument(docId,buyerId)
        _removeCreditNoteResult.value = result
    }

    fun loadCreditNoteList() {
        creditNoteUseCase.fetchCreditNoteList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _creditNoteListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _creditNoteListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _creditNoteListState.value = ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }
}