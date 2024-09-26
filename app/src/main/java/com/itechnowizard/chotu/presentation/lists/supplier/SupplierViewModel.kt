package com.itechnowizard.chotu.presentation.lists.supplier

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.domain.usecase.SupplierUseCase
import com.itechnowizard.chotu.utils.Resource
import com.itechnowizard.chotu.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val supplierUseCase: SupplierUseCase
) : ViewModel() {

    private val _addSupplierDetailsResult = MutableLiveData<Resource<Unit>>()
    val addSupplierDetailsResult: LiveData<Resource<Unit>> = _addSupplierDetailsResult

    private val _supplyDetailsState = MutableLiveData<State<SupplierModel>>()
    val supplyDetailsState: LiveData<State<SupplierModel>> = _supplyDetailsState

    fun saveSupplierDetails(imageUri: Uri?,supplier: SupplierModel) {
        val result = supplierUseCase.execute(imageUri,supplier)
        _addSupplierDetailsResult.value = result
    }

    fun loadSupplierDetails() {

        supplierUseCase.fetchDetails().onEach { result->
            when(result){
                is Resource.Success ->{
                    _supplyDetailsState.value = State(data = result.data)
                }
                is Resource.Error ->{
                    _supplyDetailsState.value = State(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _supplyDetailsState.value = State(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)

    }



}