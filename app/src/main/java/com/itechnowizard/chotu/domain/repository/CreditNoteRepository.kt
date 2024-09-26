package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.*

interface CreditNoteRepository {


    fun addCreditNote(
        creditNote: CreditNoteModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    )

    suspend fun getALlCreditNote() : QuerySnapshot

    fun deleteCreditNote(docId: String, buyerId: String)
}