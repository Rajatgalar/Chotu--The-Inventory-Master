package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.repository.TransportListRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransportListUseCase @Inject constructor(
    private val transportListRepository: TransportListRepository
) {
    fun sendData(transport: TransportListModel,isForUpdate : Boolean, documentId : String) : Resource<Unit>{
        return try{
            transportListRepository.addTransport(transport,isForUpdate,documentId)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchTransportList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = transportListRepository.getALlTransport()
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String) : Resource<Unit>{
        return try {
            transportListRepository.deleteTransport(docId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

