package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.PaymentReceiptModel
import com.itechnowizard.chotu.domain.repository.PaymentReceiptRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PaymentReceiptUseCase @Inject constructor(
    private val receiptRepository: PaymentReceiptRepository
) {
    fun sendData(
        receipt: PaymentReceiptModel,
        isForUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double
    ) : Resource<Unit>{
        return try{
            receiptRepository.addPaymentReceipt(receipt,isForUpdate,documentId,previousBillFinalAmount)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchPaymentReceiptList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = receiptRepository.getALlPaymentReceipt()
            emit(Resource.Success(buyer))
        }
        catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String, buyerId: String) : Resource<Unit>{
        return try {
            receiptRepository.deletePaymentReceipt(docId,buyerId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

    fun fetchInvoiceList(buyerId: String) : Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = receiptRepository.getAllInvoices(buyerId)
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

}

