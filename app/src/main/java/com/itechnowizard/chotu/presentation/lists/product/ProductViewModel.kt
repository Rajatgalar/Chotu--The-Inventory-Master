package com.itechnowizard.chotu.presentation.lists.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.usecase.ProductUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productUseCase: ProductUseCase
) : ViewModel(){
    private val _addProductResult = MutableLiveData<Resource<Unit>>()
    val addProductResult: LiveData<Resource<Unit>> = _addProductResult

    private val _removeProductResult = MutableLiveData<Resource<Unit>>()
    val removeProductResult: LiveData<Resource<Unit>> = _removeProductResult

    private val _productListState = MutableLiveData<ListState<QuerySnapshot>>()
    val productListState: LiveData<ListState<QuerySnapshot>> = _productListState

    fun addProduct(product: ProductModel,isForUpdate : Boolean, documentId : String) {
        val result = productUseCase.sendData(product,isForUpdate,documentId)
        _addProductResult.value = result
    }

    fun deleteProductDocument(docId: String){
        val result = productUseCase.deleteDocument(docId)
        _removeProductResult.value = result
    }

    fun loadProductList() {
        productUseCase.fetchProductList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _productListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _productListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _productListState.value = ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

}