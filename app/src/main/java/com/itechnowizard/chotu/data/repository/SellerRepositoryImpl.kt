package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.model.SellerModel
import com.itechnowizard.chotu.domain.repository.SellerRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SellerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : SellerRepository{

    private var userId: String = auth.currentUser!!.uid

    override fun addSeller(seller: SellerModel,isForUpdate : Boolean, documentId : String) {
        if(isForUpdate){
            val sellerRef = firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.SELLER)
                .document(documentId)

            firestore.runTransaction { transaction ->
                transaction.set(sellerRef,seller)

                val ledgerBuyerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.CREDITOR)
                    .document(sellerRef.id)

                transaction.update(ledgerBuyerRef,Constants.FIREBASE_NAME,seller.companyName)
            }
        }else {
            val sellerRef = firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.SELLER)
                .document()

            firestore.runTransaction { transaction ->
                transaction.set(sellerRef,seller)

                val ledgerBuyerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.CREDITOR)
                    .document(sellerRef.id)
                transaction.set(ledgerBuyerRef,
                    CreditorDebitorModel(name = seller.companyName, totalAmount=0.0)
                )
            }
        }
    }

    override suspend fun getALlSeller(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SELLER).get().await()
        collection
    }

    override fun deleteSeller(docId: String) {
        firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SELLER)
            .document(docId)
            .delete()
    }
}