package com.itechnowizard.chotu.presentation.lists.bank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BankModel
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.usecase.BankUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class BankViewModel @Inject constructor(
    private val bankUseCase: BankUseCase
) : ViewModel() {
    private val _addBankResult = MutableLiveData<Resource<Unit>>()
    val addBankResult: LiveData<Resource<Unit>> = _addBankResult

    private val _removeBankResult = MutableLiveData<Resource<Unit>>()
    val removeBankResult: LiveData<Resource<Unit>> = _removeBankResult

    private val _bankListState = MutableLiveData<ListState<QuerySnapshot>>()
    val bankListState: LiveData<ListState<QuerySnapshot>> = _bankListState

    fun addBank(bank: BankModel,isForUpdate : Boolean, documentId : String) {
        val result = bankUseCase.sendData(bank,isForUpdate,documentId)
        _addBankResult.value = result
    }

    fun deleteBankDocument(docId: String){
        val result = bankUseCase.deleteDocument(docId)
        _removeBankResult.value = result
    }

    fun loadBankList() {
        bankUseCase.fetchBankList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _bankListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _bankListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _bankListState.value =ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }


}