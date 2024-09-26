package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.repository.BuyerRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BuyerUseCase @Inject constructor(
    private val buyerRepository: BuyerRepository
) {
    fun sendData(buyer: BuyerModel,isForUpdate : Boolean, documentId : String) : Resource<Unit>{
        return try{
            buyerRepository.addBuyer(buyer,isForUpdate,documentId)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchBuyerList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = buyerRepository.getALlBuyer()
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String) : Resource<Unit>{
        return try {
            buyerRepository.deleteBuyer(docId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

