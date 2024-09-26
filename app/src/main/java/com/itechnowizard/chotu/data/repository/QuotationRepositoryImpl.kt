package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.domain.repository.QuotationRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QuotationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : QuotationRepository {

    private var userId: String = auth.currentUser!!.uid

    override fun addQuotation(
        quotation: QuotationModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
    ) {
        if (bitmapByteData != null) {
            sendImageToDatabase(bitmapByteData, quotation, isForUpdate, documentId)
        } else {
            sendData(quotation, isForUpdate, documentId)
        }
    }

    private fun sendImageToDatabase(
        bitmapByteData: ByteArray,
        quotation: QuotationModel,
        forUpdate: Boolean,
        documentId: String
    ) {

        val quotationCode = quotation.invoiceCode?.takeIf { it.isNotBlank() }
            ?: "$userId-${System.currentTimeMillis()}.jpg"

        val imageRef = storage.reference.child("$userId/quotation/$quotationCode")

        val uploadTask = imageRef.putBytes(bitmapByteData)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                quotation.imageUrl = downloadUrl
                quotation.invoiceCode = quotationCode
                sendData(quotation, forUpdate, documentId)
            }
        }
    }

    private fun sendData(
        quotation: QuotationModel,
        forUpdate: Boolean,
        quotationDocId: String         //doc Id is the current Id of the quotation if it exist or else it will be blank
    ) {

        firestore.runTransaction { transaction ->
            // Add the quotation document
            if (forUpdate) {
                val quotationRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.QUOTATION)
                    .document(quotationDocId)
                transaction.set(quotationRef, quotation)

            } else { // if the quotation is new then new quotation should be created
                val quotationRef =
                    firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.QUOTATION)
                        .document()
                transaction.set(quotationRef, quotation)
            }
        }

    }

    override suspend fun getAllQuotation(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.QUOTATION).get().await()
        collection
    }

    override fun deleteQuotation(quotationDocId: String) {

        val quotationRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.QUOTATION)
            .document(quotationDocId)
        
        firestore.runTransaction { transaction->
            transaction.delete(quotationRef)
        }
    }
}