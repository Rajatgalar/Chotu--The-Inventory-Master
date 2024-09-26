package com.itechnowizard.chotu.presentation.lists.buyer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.usecase.BuyerUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BuyerViewModel @Inject constructor(
    private val buyerUseCase: BuyerUseCase
) : ViewModel() {
    private val _addBuyerResult = MutableLiveData<Resource<Unit>>()
    val addBuyerResult: LiveData<Resource<Unit>> = _addBuyerResult

    private val _removeBuyerResult = MutableLiveData<Resource<Unit>>()
    val removeBuyerResult: LiveData<Resource<Unit>> = _removeBuyerResult

    private val _buyerListState = MutableLiveData<ListState<QuerySnapshot>>()
    val buyerListState: LiveData<ListState<QuerySnapshot>> = _buyerListState

    fun addBuyer(buyer: BuyerModel,isForUpdate : Boolean, documentId : String) {
        val result = buyerUseCase.sendData(buyer,isForUpdate,documentId)
        _addBuyerResult.value = result
    }

    fun deleteBuyerDocument(docId: String){
        val result = buyerUseCase.deleteDocument(docId)
        _removeBuyerResult.value = result
    }

    fun loadBuyerList() {
        buyerUseCase.fetchBuyerList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _buyerListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _buyerListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _buyerListState.value =ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }


}