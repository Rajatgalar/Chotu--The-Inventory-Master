package com.itechnowizard.chotu.presentation.ledger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.LedgerModel
import com.itechnowizard.chotu.domain.usecase.InvoiceUseCase
import com.itechnowizard.chotu.domain.usecase.LedgerUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.ListStateAny
import com.itechnowizard.chotu.utils.State
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val ledgerUseCase: LedgerUseCase
) : ViewModel() {
    
    private val _ledgerState = MutableLiveData<State<LedgerModel>>()
    val ledgerState: LiveData<State<LedgerModel>> = _ledgerState

    private val _detailLedgerState = MutableLiveData<ListStateAny<BuyerSellerLedgerModel>>()
    val detailLedgerState: LiveData<ListStateAny<BuyerSellerLedgerModel>> = _detailLedgerState

    fun loadLedger() {
        ledgerUseCase.fetchLedger().onEach { result->
            when(result){
                is Resource.Success ->{
                    _ledgerState.value = State(data = result.data)
                }
                is Resource.Error ->{
                    _ledgerState.value = State(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _ledgerState.value = State(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

    fun loadDetailLedger(buyerId: String, isBuyer: Boolean) {
        ledgerUseCase.fetchDetailLedger(buyerId, isBuyer).onEach { result->
            when(result){
                is Resource.Success ->{
                    _detailLedgerState.value = ListStateAny(list = result.data)
                }
                is Resource.Error ->{
                    _detailLedgerState.value = ListStateAny(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _detailLedgerState.value = ListStateAny(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }


}