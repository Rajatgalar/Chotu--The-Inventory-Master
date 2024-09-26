package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.QuotationModel
import com.itechnowizard.chotu.domain.repository.QuotationRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuotationUseCase @Inject constructor(
    private val buyerRepository: QuotationRepository
) {
    fun sendData(
        invoice: QuotationModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?
    ) : Resource<Unit>{
        return try{
            buyerRepository.addQuotation(invoice,isForUpdate,documentId,bitmapByteData)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchQuotationList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = buyerRepository.getAllQuotation()
            emit(Resource.Success(buyer))
        }
        catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String) : Resource<Unit>{
        return try {
            buyerRepository.deleteQuotation(docId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

