package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.LedgerModel
import com.itechnowizard.chotu.domain.model.ReportModel
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.repository.LedgerRepository
import com.itechnowizard.chotu.domain.repository.ReportRepository
import com.itechnowizard.chotu.domain.repository.TransportListRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportUseCase @Inject constructor(
    private val ledgerRepository: ReportRepository
) {


    fun fetchReport(): Flow<Resource<ReportModel>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = ledgerRepository.getReport()
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }


}

