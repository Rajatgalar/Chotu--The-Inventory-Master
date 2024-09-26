package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.DebitNoteModel
import com.itechnowizard.chotu.domain.repository.DebitNoteRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DebitNoteUseCase @Inject constructor(
    private val buyerRepository: DebitNoteRepository
) {
    fun sendData(
        debitNote: DebitNoteModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    ) : Resource<Unit>{
        return try{
            buyerRepository.addDebitNote(debitNote,isForUpdate,documentId,bitmapByteData, previousBillFinalAmount)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchDebitNoteList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = buyerRepository.getALlDebitNote()
            emit(Resource.Success(buyer))
        }
        catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String, sellerId: String) : Resource<Unit>{
        return try {
            buyerRepository.deleteDebitNote(docId,sellerId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

