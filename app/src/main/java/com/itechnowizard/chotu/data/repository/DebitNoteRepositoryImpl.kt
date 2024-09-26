package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.DebitNoteModel
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.repository.DebitNoteRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DebitNoteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : DebitNoteRepository {

    private var userId: String = auth.currentUser!!.uid

    override fun addDebitNote(
        debitNote: DebitNoteModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double,
    ) {
        if (bitmapByteData != null) {
            sendImageToDatabase(bitmapByteData, debitNote, isForUpdate, documentId, previousBillFinalAmount)
        } else {
            sendData(debitNote, isForUpdate, documentId, previousBillFinalAmount)
        }
    }

    private fun sendImageToDatabase(
        bitmapByteData: ByteArray,
        debitNote: DebitNoteModel,
        forUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double
    ) {

        val debitNoteCode = debitNote.debitNoteCode?.takeIf { it.isNotBlank() }
            ?: "$userId-${System.currentTimeMillis()}.jpg"

        val imageRef = storage.reference.child("$userId/debitNote/$debitNoteCode")

        val uploadTask = imageRef.putBytes(bitmapByteData)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                debitNote.imageUrl = downloadUrl
                debitNote.debitNoteCode = debitNoteCode
//                Constants.DOWNLOAD_URL = downloadUrl
                sendData(debitNote, forUpdate, documentId, previousBillFinalAmount)
            }
        }
    }

    private fun sendData(
        debitNote: DebitNoteModel,
        forUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double,
    ) {

        firestore.runTransaction { transaction ->
            // Update the inventory quantities

            val ledgerRef = firestore.collection(userId)
                .document(Constants.FIREBASE_LEDGER)
                .collection(Constants.CREDITOR)
                .document(debitNote.sellerId!!)

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            var amount = 0.0

            // Add the debitNote document
            if (forUpdate) {
                val debitNoteRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.DEBITNOTE)
                    .document(documentId)
                transaction.set(debitNoteRef, debitNote)

                if(previousBillFinalAmount != debitNote.billFinalAmount){

                    amount = if(previousBillFinalAmount>debitNote.billFinalAmount!!){
                        totalAmount + previousBillFinalAmount-debitNote.billFinalAmount!!
                    }else{
                        totalAmount - debitNote.billFinalAmount!! + previousBillFinalAmount
                    }

                    transaction.set(ledgerRef, CreditorDebitorModel(name = debitNote.sellerDetail!!.companyName, totalAmount=amount))

                    val sellerRef = firestore.collection(userId)
                        .document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.SELLER)
                        .document(debitNote.sellerId)
                        .collection(Constants.DEBITNOTE)
                        .document(documentId)

                    transaction.set(sellerRef, BuyerSellerLedgerModel(
                        date = debitNote.debitNoteDate,
                        invoiceNumber = debitNote.debitNoteNumber,
                        totalAmount = debitNote.billFinalAmount,
                        type = Constants.DEBITNOTE
                    )
                    )
                }else
                    return@runTransaction
            } else {
                val debitNoteRef =
                    firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.DEBITNOTE)
                        .document()
                transaction.set(debitNoteRef, debitNote)

                //Below we are creating buyerRef to the invoices linked with this buyer and adding the model
                val sellerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.SELLER)
                    .document(debitNote.sellerId)
                    .collection(Constants.DEBITNOTE)
                    .document(debitNoteRef.id)

                transaction.set(sellerRef, BuyerSellerLedgerModel(
                    date = debitNote.debitNoteDate,
                    invoiceNumber = debitNote.debitNoteNumber,
                    totalAmount = debitNote.billFinalAmount,
                    type = Constants.DEBITNOTE
                )
                )

                //This will add the new amount to the seller id total amount in ledger
                amount = totalAmount - debitNote.billFinalAmount!!
                transaction.set(ledgerRef, CreditorDebitorModel(name = debitNote.sellerDetail!!.companyName,totalAmount= amount))

                val ledgerInvoiceRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.CREDITOR)
                    .document(debitNote.sellerId)
                    .collection(Constants.DEBITNOTE)
                    .document(debitNoteRef.id)

                transaction.set(ledgerInvoiceRef, hashMapOf<String, Any>())
            }
        }

    }

    override suspend fun getALlDebitNote(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.DEBITNOTE).get().await()
        collection
    }

    override fun deleteDebitNote(docId: String,sellerId : String) {
        val debitNoteRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.DEBITNOTE)
            .document(docId)

        val ledgerPurchaseRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR)
            .document(sellerId)
            .collection(Constants.DEBITNOTE)
            .document(debitNoteRef.id)

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR)
            .document(sellerId)

        val sellerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SELLER)
            .document(sellerId)
            .collection(Constants.DEBITNOTE)
            .document(docId)

        firestore.runTransaction { transaction->
            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            val currentInvoiceValue = transaction.get(debitNoteRef).getDouble("billFinalAmount") ?: 0.0
            val amount = totalAmount + currentInvoiceValue

            transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

            transaction.delete(debitNoteRef)
            transaction.delete(ledgerPurchaseRef)
            transaction.delete(sellerRef)
        }
    }
}