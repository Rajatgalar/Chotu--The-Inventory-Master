package com.itechnowizard.chotu.presentation.lists.transport.transportlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.usecase.TransportListUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TransportListViewModel @Inject constructor(
    private val transportListUseCase: TransportListUseCase
) : ViewModel() {
    private val _addTransportResult = MutableLiveData<Resource<Unit>>()
    val addTransportResult: LiveData<Resource<Unit>> = _addTransportResult

    private val _removeTransportResult = MutableLiveData<Resource<Unit>>()
    val removeTransportResult: LiveData<Resource<Unit>> = _removeTransportResult

    private val _transportListState = MutableLiveData<ListState<QuerySnapshot>>()
    val transportListState: LiveData<ListState<QuerySnapshot>> = _transportListState

    fun addTransport(transport: TransportListModel, isForUpdate : Boolean, documentId : String) {
        val result = transportListUseCase.sendData(transport,isForUpdate,documentId)
        _addTransportResult.value = result
    }

    fun deleteTransportDocument(docId: String){
        val result = transportListUseCase.deleteDocument(docId)
        _removeTransportResult.value = result
    }

    fun loadTransportList() {
        transportListUseCase.fetchTransportList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _transportListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _transportListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _transportListState.value =ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }


}