package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.*

interface PaymentReceiptRepository {
    
    fun addPaymentReceipt(
        receipt: PaymentReceiptModel,
        forUpdate: Boolean,
        receiptDocumentId: String,
        previousBillFinalAmount: Double
    )

    suspend fun getALlPaymentReceipt() : QuerySnapshot

    fun deletePaymentReceipt(docId: String, buyerId: String)

    suspend fun getAllInvoices(buyerId : String) : QuerySnapshot
}