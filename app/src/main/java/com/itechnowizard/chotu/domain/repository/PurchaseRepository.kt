package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.presentation.purchase.addpurchase.AddPurchase

interface PurchaseRepository {


    fun addPurchase(
        purchase: PurchaseModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    )

    suspend fun getALLPurchase() : QuerySnapshot

    fun deletePurchase(docId: String, sellerId: String)
}