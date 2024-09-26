package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.*

interface DebitNoteRepository {


    fun addDebitNote(
        debitNote: DebitNoteModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    )

    suspend fun getALlDebitNote() : QuerySnapshot

    fun deleteDebitNote(docId: String, sellerId: String)
}