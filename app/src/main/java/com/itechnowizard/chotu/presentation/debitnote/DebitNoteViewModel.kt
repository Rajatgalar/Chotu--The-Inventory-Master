package com.itechnowizard.chotu.presentation.debitnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.DebitNoteModel
import com.itechnowizard.chotu.domain.usecase.DebitNoteUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DebitNoteViewModel @Inject constructor(
    private val debitNoteUseCase: DebitNoteUseCase
) : ViewModel() {
    private val _addDebitNoteResult = MutableLiveData<Resource<Unit>>()
    val addDebitNoteResult: LiveData<Resource<Unit>> = _addDebitNoteResult

    private val _removeDebitNoteResult = MutableLiveData<Resource<Unit>>()
    val removeDebitNoteResult: LiveData<Resource<Unit>> = _removeDebitNoteResult

    private val _debitNoteListState = MutableLiveData<ListState<QuerySnapshot>>()
    val debitNoteListState: LiveData<ListState<QuerySnapshot>> = _debitNoteListState

    fun addDebitNote(
        debitNoteModel: DebitNoteModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double,
    ) {
        val result = debitNoteUseCase.sendData(debitNoteModel,isForUpdate,documentId,bitmapByteData, previousBillFinalAmount)
        _addDebitNoteResult.value = result
    }

    fun deleteDebitNoteDocument(docId: String, sellerId: String){
        val result = debitNoteUseCase.deleteDocument(docId,sellerId)
        _removeDebitNoteResult.value = result
    }

    fun loadDebitNoteList() {
        debitNoteUseCase.fetchDebitNoteList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _debitNoteListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _debitNoteListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _debitNoteListState.value = ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }
}