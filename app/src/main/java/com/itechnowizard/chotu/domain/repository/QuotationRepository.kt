package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.*

interface QuotationRepository {


    fun addQuotation(
        quotation: QuotationModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?
    )

    suspend fun getAllQuotation() : QuerySnapshot

    fun deleteQuotation(quotationDocId: String)
}