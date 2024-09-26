package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.PaymentMadeModel
import com.itechnowizard.chotu.domain.repository.PaymentMadeRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PaymentMadeUseCase @Inject constructor(
    private val receiptRepository: PaymentMadeRepository
) {
    fun sendData(
        receipt: PaymentMadeModel,
        isForUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double
    ) : Resource<Unit>{
        return try{
            receiptRepository.addPaymentMade(receipt,isForUpdate,documentId,previousBillFinalAmount)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchPaymentMadeList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = receiptRepository.getALlPaymentMade()
            emit(Resource.Success(buyer))
        }
        catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String, sellerId: String) : Resource<Unit>{
        return try {
            receiptRepository.deletePaymentMade(docId,sellerId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

    fun fetchPurchaseList(sellerId: String) : Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = receiptRepository.getAllPurchases(sellerId)
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

}

