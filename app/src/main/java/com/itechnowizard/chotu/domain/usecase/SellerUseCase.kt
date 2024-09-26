package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.SellerModel
import com.itechnowizard.chotu.domain.repository.SellerRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SellerUseCase @Inject constructor(
    private val sellerRepository: SellerRepository
) {
    fun sendData(seller: SellerModel,isForUpdate : Boolean, documentId : String) : Resource<Unit>{
        return try{
            sellerRepository.addSeller(seller,isForUpdate,documentId)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchSellerList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val seller = sellerRepository.getALlSeller()
            emit(Resource.Success(seller))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String) : Resource<Unit>{
        return try {
            sellerRepository.deleteSeller(docId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

