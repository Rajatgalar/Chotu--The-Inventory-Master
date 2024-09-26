package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.LedgerModel
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.repository.LedgerRepository
import com.itechnowizard.chotu.domain.repository.TransportListRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LedgerUseCase @Inject constructor(
    private val ledgerRepository: LedgerRepository
) {


    fun fetchLedger(): Flow<Resource<LedgerModel>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = ledgerRepository.getLedger()
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun fetchDetailLedger(buyerId: String, isBuyer: Boolean): Flow<Resource<List<BuyerSellerLedgerModel>>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = ledgerRepository.getDetailLedger(buyerId, isBuyer)
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

}

