package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.InventoryModel
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.repository.InvoiceRepository
import com.itechnowizard.chotu.domain.repository.ProformaInvoiceRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProformaInvoiceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ProformaInvoiceRepository {

    private var userId: String = auth.currentUser!!.uid

    override fun addInvoice(
        invoice: InvoiceModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double,
    ) {
        if (bitmapByteData != null) {
            sendImageToDatabase(bitmapByteData, invoice, isForUpdate, documentId,previousBillFinalAmount)
        } else {
            sendData(invoice, isForUpdate, documentId,previousBillFinalAmount)
        }
    }

    private fun sendImageToDatabase(
        bitmapByteData: ByteArray,
        invoice: InvoiceModel,
        forUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double
    ) {

        val invoiceCode = invoice.invoiceCode?.takeIf { it.isNotBlank() }
            ?: "$userId-${System.currentTimeMillis()}.jpg"

        val imageRef = storage.reference.child("$userId/proforma/$invoiceCode")

        val uploadTask = imageRef.putBytes(bitmapByteData)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                invoice.imageUrl = downloadUrl
                invoice.invoiceCode = invoiceCode
//                Constants.DOWNLOAD_URL = downloadUrl
                sendData(invoice, forUpdate, documentId, previousBillFinalAmount)
            }
        }
    }

    private fun sendData(
        invoice: InvoiceModel,
        forUpdate: Boolean,
        invoiceDocId: String,         //doc Id is the current Id of the invoice if it exist or else it will be blank
        previousBillFinalAmount: Double,
    ) {

        firestore.runTransaction { transaction ->
            // Add the invoice document
            if (forUpdate) {
                val invoiceRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.PROFORMAINVOICE)
                    .document(invoiceDocId)
                transaction.set(invoiceRef, invoice)

            } else { // if the invoice is new then new invoice should be created
                val invoiceRef =
                    firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.PROFORMAINVOICE)
                        .document()
                transaction.set(invoiceRef, invoice)
            }
        }

    }

    override suspend fun getALlInvoice(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PROFORMAINVOICE).get().await()
        collection
    }

    override fun deleteInvoice(invoiceDocId: String) {

        val invoiceRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PROFORMAINVOICE)
            .document(invoiceDocId)
        
        firestore.runTransaction { transaction->
            transaction.delete(invoiceRef)
        }
    }
}