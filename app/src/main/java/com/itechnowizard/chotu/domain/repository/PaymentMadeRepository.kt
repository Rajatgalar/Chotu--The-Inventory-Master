package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.*

interface PaymentMadeRepository {
    
    fun addPaymentMade(
        receipt: PaymentMadeModel,
        forUpdate: Boolean,
        receiptDocumentId: String,
        previousBillFinalAmount: Double
    )

    suspend fun getALlPaymentMade() : QuerySnapshot

    fun deletePaymentMade(payMadeDocumentId: String, sellerId: String)

    suspend fun getAllPurchases(sellerId : String) : QuerySnapshot
}