package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.repository.InvoiceRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InvoiceUseCase @Inject constructor(
    private val buyerRepository: InvoiceRepository
) {
    fun sendData(
        invoice: InvoiceModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    ) : Resource<Unit>{
        return try{
            buyerRepository.addInvoice(invoice,isForUpdate,documentId,bitmapByteData,previousBillFinalAmount)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchInvoiceList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = buyerRepository.getALlInvoice()
            emit(Resource.Success(buyer))
        }
        catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String, buyerId: String) : Resource<Unit>{
        return try {
            buyerRepository.deleteInvoice(docId,buyerId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

