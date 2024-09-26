package com.itechnowizard.chotu.presentation.expiry

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ExpiryModel
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.usecase.ExpiryUseCase
import com.itechnowizard.chotu.domain.usecase.ProductUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.ListStateAny
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ExpiryViewModel @Inject constructor(
    private val productUseCase: ExpiryUseCase
) : ViewModel(){

    private val _productListState = MutableLiveData<ListStateAny<ExpiryModel>>()
    val productListState: LiveData<ListStateAny<ExpiryModel>> = _productListState

    fun loadProductList() {
        productUseCase.fetchProductList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _productListState.value = ListStateAny(list = result.data)
                }
                is Resource.Error ->{
                    _productListState.value = ListStateAny(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _productListState.value = ListStateAny(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

}