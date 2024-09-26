package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.repository.BuyerRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BuyerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : BuyerRepository{

    private var userId: String = auth.currentUser!!.uid

    override fun addBuyer(buyer: BuyerModel,isForUpdate : Boolean, documentId : String) {
        if(isForUpdate){
            val buyerRef = firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.BUYER)
                .document(documentId)

            firestore.runTransaction { transaction ->
                transaction.set(buyerRef,buyer)

                val ledgerBuyerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.DEBTORS)
                    .document(buyerRef.id)

                transaction.update(ledgerBuyerRef,Constants.FIREBASE_NAME,buyer.companyName)

            }
        }else {
            val buyerRef = firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.BUYER)
                .document()

            firestore.runTransaction { transaction ->
                transaction.set(buyerRef,buyer)

                val ledgerBuyerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.DEBTORS)
                    .document(buyerRef.id)
                transaction.set(ledgerBuyerRef,
                    CreditorDebitorModel(id = "",buyer.companyName, 0.0)
                )
            }
        }
    }

    override suspend fun getALlBuyer(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BUYER).get().await()
        collection
    }

    override fun deleteBuyer(docId: String) {
        firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BUYER)
            .document(docId)
            .delete()
    }
}