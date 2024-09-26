package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.*

interface ProformaInvoiceRepository {


    fun addInvoice(
        invoice: InvoiceModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    )

    suspend fun getALlInvoice() : QuerySnapshot

    fun deleteInvoice(invoiceDocId: String)
}