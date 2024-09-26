package com.itechnowizard.chotu.presentation.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.InventoryModel
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.usecase.InventoryUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import com.itechnowizard.chotu.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryUseCase: InventoryUseCase
) : ViewModel() {
    private val _addInventoryResult = MutableLiveData<Resource<Unit>>()
    val addInventoryResult: LiveData<Resource<Unit>> = _addInventoryResult

    private val _removeInventoryResult = MutableLiveData<Resource<Unit>>()
    val removeInventoryResult: LiveData<Resource<Unit>> = _removeInventoryResult

    private val _inventoryListState = MutableLiveData<State<List<ProductModel>>>()
    val inventoryListState: LiveData<State<List<ProductModel>>> = _inventoryListState

    fun addInventory(inventory: InventoryModel, isForUpdate : Boolean, documentId : String) {
        val result = inventoryUseCase.sendData(inventory,isForUpdate,documentId)
        _addInventoryResult.value = result
    }

    fun loadInventoryProduct() {

        inventoryUseCase.getallProduct().onEach { result->
            when(result){
                is Resource.Success ->{
                    _inventoryListState.value = State(data = result.data)
                }
                is Resource.Error ->{
                    _inventoryListState.value = State(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _inventoryListState.value = State(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

    fun deleteInventoryDocument(docId: String){
        val result = inventoryUseCase.deleteDocument(docId)
        _removeInventoryResult.value = result
    }




}