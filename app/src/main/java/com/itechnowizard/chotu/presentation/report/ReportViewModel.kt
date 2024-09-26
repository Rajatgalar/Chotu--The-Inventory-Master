package com.itechnowizard.chotu.presentation.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.LedgerModel
import com.itechnowizard.chotu.domain.model.ReportModel
import com.itechnowizard.chotu.domain.usecase.InvoiceUseCase
import com.itechnowizard.chotu.domain.usecase.LedgerUseCase
import com.itechnowizard.chotu.domain.usecase.ReportUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.ListStateAny
import com.itechnowizard.chotu.utils.State
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportUseCase: ReportUseCase
) : ViewModel() {
    
    private val _reportState = MutableLiveData<State<ReportModel>>()
    val reportState: LiveData<State<ReportModel>> = _reportState

    private val _detailLedgerState = MutableLiveData<ListStateAny<BuyerSellerLedgerModel>>()
    val detailReportState: LiveData<ListStateAny<BuyerSellerLedgerModel>> = _detailLedgerState

    fun loadReport() {
        reportUseCase.fetchReport().onEach { result->
            when(result){
                is Resource.Success ->{
                    _reportState.value = State(data = result.data)
                }
                is Resource.Error ->{
                    _reportState.value = State(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _reportState.value = State(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

}