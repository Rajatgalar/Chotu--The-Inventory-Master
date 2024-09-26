package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.PurchaseModel
import com.itechnowizard.chotu.domain.repository.PurchaseRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class PurchaseUseCase @Inject constructor(
    private val buyerRepository: PurchaseRepository
) {
    fun sendData(
        invoice: PurchaseModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    ) : Resource<Unit>{
        return try{
            buyerRepository.addPurchase(invoice,isForUpdate,documentId,bitmapByteData, previousBillFinalAmount)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchInvoiceList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = buyerRepository.getALLPurchase()
            emit(Resource.Success(buyer))
        }
        catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String, sellerId: String) : Resource<Unit>{
        return try {
            buyerRepository.deletePurchase(docId, sellerId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

