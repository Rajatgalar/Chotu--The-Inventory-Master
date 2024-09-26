package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.model.PaymentMadeModel
import com.itechnowizard.chotu.domain.repository.PaymentMadeRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentMadeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
) : PaymentMadeRepository {

    private var userId: String = auth.currentUser!!.uid

    override fun addPaymentMade(
        payMade: PaymentMadeModel,
        forUpdate: Boolean,
        payMadeDocumentId: String,
        previousBillFinalAmount: Double
    ) {

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR)
            .document(payMade.sellerId!!)

        firestore.runTransaction { transaction ->

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            var amount = 0.0

            if(forUpdate){
                val payMadeRef =  firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.PAYMENTMADE)
                    .document(payMadeDocumentId)

                transaction.set(payMadeRef,payMade)

                //Setting the changed value of total amount and this will
                //update the total amount in ledger
                if(previousBillFinalAmount != payMade.totalAmount){

                    amount = if(previousBillFinalAmount>payMade.totalAmount!!)
                        totalAmount + previousBillFinalAmount - payMade.totalAmount
                    else
                        totalAmount - payMade.totalAmount + previousBillFinalAmount

                    transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

                    //here we will udpate the seller also
                    val sellerRef = firestore.collection(userId)
                        .document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.SELLER)
                        .document(payMade.sellerId)
                        .collection(Constants.PAYMENTMADE)
                        .document(payMadeDocumentId)

                    transaction.set(sellerRef, BuyerSellerLedgerModel(
                        date = payMade.paymentDate,
                        invoiceNumber =  payMade.purchaseNumber,
                        totalAmount = payMade.totalAmount,
                        type = Constants.PAYMENTMADE
                    )
                    )

                } else {
                    return@runTransaction
                }

            }else{
                val payMadeRef =  firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.PAYMENTMADE)
                    .document()

                transaction.set(payMadeRef,payMade)

                //here we will udpate the seller also
                val sellerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.SELLER)
                    .document(payMade.sellerId)
                    .collection(Constants.PAYMENTMADE)
                    .document(payMadeRef.id)

                transaction.set(sellerRef, BuyerSellerLedgerModel(
                    date = payMade.paymentDate,
                    invoiceNumber = payMade.purchaseNumber,
                    totalAmount = payMade.totalAmount,
                    type = Constants.PAYMENTMADE
                )
                )

                //This will subtract the new amount to the seller id total amount in ledger
                amount = totalAmount - payMade.totalAmount!!
                transaction.set(ledgerRef, CreditorDebitorModel(name = payMade.sellerName, totalAmount=amount))

                //this will add the invoice id to the ledger to that sellerID
                val ledgerPayMadeRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.CREDITOR)
                    .document(payMade.sellerId)
                    .collection(Constants.PAYMENTMADE)
                    .document(payMadeRef.id)

                transaction.set(ledgerPayMadeRef, hashMapOf<String, Any>())

            }
        }

    }

    override suspend fun getALlPaymentMade(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PAYMENTMADE).get().await()
        collection
    }

    override fun deletePaymentMade(payMadeDocumentId: String, sellerId: String) {

        val payMadeRef =  firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PAYMENTMADE)
            .document(payMadeDocumentId)

        val ledgerPaymentMadeRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR)
            .document(sellerId)
            .collection(Constants.PAYMENTMADE)
            .document(payMadeRef.id)

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR)
            .document(sellerId)

        val sellerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SELLER)
            .document(sellerId)
            .collection(Constants.PAYMENTMADE)
            .document(payMadeDocumentId)

        firestore.runTransaction { transaction->

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            val currentPaymentMadeValue = transaction.get(payMadeRef).getDouble("totalAmount") ?: 0.0

            val amount = totalAmount + currentPaymentMadeValue

            transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

            transaction.delete(payMadeRef)
            transaction.delete(ledgerPaymentMadeRef)
            transaction.delete(sellerRef)
        }
    }

    override suspend fun getAllPurchases(sellerId: String): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SELLER)
            .document(sellerId)
            .collection(Constants.PURCHASE)
            .get().await()
        collection
    }
}