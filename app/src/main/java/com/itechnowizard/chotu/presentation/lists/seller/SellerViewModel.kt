package com.itechnowizard.chotu.presentation.lists.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.SellerModel
import com.itechnowizard.chotu.domain.usecase.SellerUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class SellerViewModel @Inject constructor(
    private val sellerUseCase: SellerUseCase
) : ViewModel() {
    private val _addSellerResult = MutableLiveData<Resource<Unit>>()
    val addSellerResult: LiveData<Resource<Unit>> = _addSellerResult

    private val _removeSellerResult = MutableLiveData<Resource<Unit>>()
    val removeSellerResult: LiveData<Resource<Unit>> = _removeSellerResult

    private val _sellerListState = MutableLiveData<ListState<QuerySnapshot>>()
    val sellerListState: LiveData<ListState<QuerySnapshot>> = _sellerListState

    fun addSeller(seller: SellerModel, isForUpdate : Boolean, documentId : String) {
        val result = sellerUseCase.sendData(seller,isForUpdate,documentId)
        _addSellerResult.value = result
    }

    fun deleteSellerDocument(docId: String){
        val result = sellerUseCase.deleteDocument(docId)
        _removeSellerResult.value = result
    }

    fun loadSellerList() {
        sellerUseCase.fetchSellerList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _sellerListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _sellerListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _sellerListState.value = ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }


}