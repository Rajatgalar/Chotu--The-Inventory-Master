package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.model.LedgerModel
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.repository.LedgerRepository
import com.itechnowizard.chotu.domain.repository.TransportListRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.Continuation

class LedgerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : LedgerRepository {

    private var userId: String = auth.currentUser!!.uid

    override suspend fun getLedger(): LedgerModel = withContext(Dispatchers.IO) {
        val creditorCollection = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR).get().await()

        val debtorCollection = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS).get().await()

        val creditorList = creditorCollection.documents.map { creditorDoc ->
            val creditor = creditorDoc.toObject<CreditorDebitorModel>()
            creditor!!.id = creditorDoc.id // Store the document ID in the model
            creditor
        }

        val debitorList = debtorCollection.documents.map { debitorDoc ->
            val debitor = debitorDoc.toObject<CreditorDebitorModel>()
            debitor!!.id = debitorDoc.id // Store the document ID in the model
            debitor
        }

        // Create a LedgerModel object with the merged lists
        LedgerModel(creditorList, debitorList)
    }

    override suspend fun getDetailLedger(buyerId: String, isBuyer: Boolean): List<BuyerSellerLedgerModel> {
        val invoicePurchase = if (isBuyer) Constants.INVOICE else Constants.PURCHASE
        val payment = if (isBuyer) Constants.PAYMENTRECEIPT else Constants.PAYMENTMADE
        val creditDebit = if (isBuyer) Constants.CREDITNOTE else Constants.DEBITNOTE
        val collection = if (isBuyer) Constants.BUYER else Constants.SELLER

        val invoiceRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(collection)
            .document(buyerId)
            .collection(invoicePurchase)
            .get().await()

        val paymentRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(collection)
            .document(buyerId)
            .collection(payment)
            .get().await()

        val creditDebitRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(collection)
            .document(buyerId)
            .collection(creditDebit)
            .get().await()

        val buyerSellerLedgerModelList = mutableListOf<BuyerSellerLedgerModel>()

        // Add all documents from the invoice collection to the list
        invoiceRef.documents.map { document ->
            val buyerSellerLedgerModel = document.toObject<BuyerSellerLedgerModel>()
            buyerSellerLedgerModel!!.id = document.id // Store the document ID in the model
            buyerSellerLedgerModelList.add(buyerSellerLedgerModel)
        }

        // Add all documents from the payment collection to the list
        paymentRef.documents.map { document ->
            val buyerSellerLedgerModel = document.toObject<BuyerSellerLedgerModel>()
            buyerSellerLedgerModel!!.id = document.id // Store the document ID in the model
            buyerSellerLedgerModelList.add(buyerSellerLedgerModel)
        }

        // Add all documents from the credit/debit note collection to the list
        creditDebitRef.documents.map { document ->
            val buyerSellerLedgerModel = document.toObject<BuyerSellerLedgerModel>()
            buyerSellerLedgerModel!!.id = document.id // Store the document ID in the model
            buyerSellerLedgerModelList.add(buyerSellerLedgerModel)
        }

        return buyerSellerLedgerModelList
    }



}