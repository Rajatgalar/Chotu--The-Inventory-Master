package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.CreditNoteModel
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.repository.CreditNoteRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreditNoteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : CreditNoteRepository {

    private var userId: String = auth.currentUser!!.uid

    override fun addCreditNote(
        creditNote: CreditNoteModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double,
    ) {
        if (bitmapByteData != null) {
            sendImageToDatabase(bitmapByteData, creditNote, isForUpdate, documentId, previousBillFinalAmount)
        } else {
            sendData(creditNote, isForUpdate, documentId, previousBillFinalAmount)
        }
    }

    private fun sendImageToDatabase(
        bitmapByteData: ByteArray,
        creditNote: CreditNoteModel,
        forUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double
    ) {

        val creditNoteCode = creditNote.creditNoteCode?.takeIf { it.isNotBlank() }
            ?: "$userId-${System.currentTimeMillis()}.jpg"

        val imageRef = storage.reference.child("$userId/creditNote/$creditNoteCode")

        val uploadTask = imageRef.putBytes(bitmapByteData)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                creditNote.imageUrl = downloadUrl
                creditNote.creditNoteCode = creditNoteCode
                sendData(creditNote, forUpdate, documentId, previousBillFinalAmount)
            }
        }
    }

    private fun sendData(
        creditNote: CreditNoteModel,
        forUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double,
    ) {

        firestore.runTransaction { transaction ->
            // Update the inventory quantities

            val ledgerRef = firestore.collection(userId)
                .document(Constants.FIREBASE_LEDGER)
                .collection(Constants.DEBTORS)
                .document(creditNote.buyerId!!)

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            var amount = 0.0

            // Add the creditNote document
            if (forUpdate) {
                val creditNoteRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.CREDITNOTE)
                    .document(documentId)
                transaction.set(creditNoteRef, creditNote)

                if(previousBillFinalAmount != creditNote.billFinalAmount){

                    amount = if(previousBillFinalAmount>creditNote.billFinalAmount!!){
                        totalAmount + previousBillFinalAmount-creditNote.billFinalAmount!!
                    }else{
                        totalAmount + creditNote.billFinalAmount!! + previousBillFinalAmount
                    }

                    transaction.set(ledgerRef, CreditorDebitorModel(name = creditNote.buyerDetail!!.companyName,totalAmount= amount))

                    val buyerRef = firestore.collection(userId)
                        .document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.BUYER)
                        .document(creditNote.buyerId)
                        .collection(Constants.CREDITNOTE)
                        .document(documentId)

                    transaction.set(buyerRef, BuyerSellerLedgerModel(
                        date = creditNote.creditNoteDate,
                        invoiceNumber = creditNote.creditNoteNumber,
                        totalAmount = creditNote.billFinalAmount,
                        type = Constants.CREDITNOTE
                    )
                    )

                }else
                    return@runTransaction
            } else {
                val creditNoteRef =
                    firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.CREDITNOTE)
                        .document()
                transaction.set(creditNoteRef, creditNote)

                val buyerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.BUYER)
                    .document(creditNote.buyerId)
                    .collection(Constants.CREDITNOTE)
                    .document(creditNoteRef.id)

                transaction.set(buyerRef, BuyerSellerLedgerModel(
                    date = creditNote.creditNoteDate,
                    invoiceNumber = creditNote.creditNoteNumber,
                    totalAmount = creditNote.billFinalAmount,
                    type = Constants.CREDITNOTE
                )
                )

                //This will add the new amount to the buyer id total amount in ledger
                amount = totalAmount - creditNote.billFinalAmount!!
                transaction.set(ledgerRef, CreditorDebitorModel(name = creditNote.buyerDetail!!.companyName,totalAmount= amount))

                val ledgerInvoiceRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.DEBTORS)
                    .document(creditNote.buyerId)
                    .collection(Constants.CREDITNOTE)
                    .document(creditNoteRef.id)

                transaction.set(ledgerInvoiceRef, hashMapOf<String, Any>())
            }
        }

    }

    override suspend fun getALlCreditNote(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.CREDITNOTE).get().await()
        collection
    }

    override fun deleteCreditNote(docId: String,buyerId : String) {
        val creditNoteRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.CREDITNOTE)
            .document(docId)

        val ledgerPurchaseRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS)
            .document(buyerId)
            .collection(Constants.CREDITNOTE)
            .document(creditNoteRef.id)

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS)
            .document(buyerId)

        val buyerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BUYER)
            .document(buyerId)
            .collection(Constants.CREDITNOTE)
            .document(docId)


        firestore.runTransaction { transaction->
            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            val currentInvoiceValue = transaction.get(creditNoteRef).getDouble("billFinalAmount") ?: 0.0
            val amount = totalAmount + currentInvoiceValue

            transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

            transaction.delete(creditNoteRef)
            transaction.delete(ledgerPurchaseRef)
            transaction.delete(buyerRef)
        }
    }
}