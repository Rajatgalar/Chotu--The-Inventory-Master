package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.model.PaymentReceiptModel
import com.itechnowizard.chotu.domain.repository.PaymentReceiptRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PaymentReceiptRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
) : PaymentReceiptRepository {

    private var userId: String = auth.currentUser!!.uid

    override fun addPaymentReceipt(
        receipt: PaymentReceiptModel,
        forUpdate: Boolean,
        receiptDocumentId: String,
        previousBillFinalAmount: Double
    ) {

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS)
            .document(receipt.buyerId!!)

        firestore.runTransaction { transaction ->

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            var amount = 0.0

            if(forUpdate){
                val receiptRef =  firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.PAYMENTRECEIPT)
                    .document(receiptDocumentId)

                transaction.set(receiptRef,receipt)

                //Setting the changed value of total amount and this will
                //update the total amount in ledger
                if(previousBillFinalAmount != receipt.totalAmount){

                    amount = if(previousBillFinalAmount>receipt.totalAmount!!)
                        totalAmount + previousBillFinalAmount - receipt.totalAmount
                    else
                        totalAmount - receipt.totalAmount + previousBillFinalAmount

                    transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

                    val buyerRef = firestore.collection(userId)
                        .document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.BUYER)
                        .document(receipt.buyerId)
                        .collection(Constants.PAYMENTRECEIPT)
                        .document(receiptDocumentId)

                    transaction.set(buyerRef, BuyerSellerLedgerModel(
                        date = receipt.paymentDate,
                        invoiceNumber = receipt.invoiceNumber,
                        totalAmount = receipt.totalAmount,
                        type = Constants.PAYMENTRECEIPT
                    )
                    )

                } else {
                    return@runTransaction
                }

            }else{
                val receiptRef =  firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.PAYMENTRECEIPT)
                    .document()

                transaction.set(receiptRef,receipt)

                //Below we are creating buyerRef to the invoices linked with this buyer and adding the model
                val buyerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.BUYER)
                    .document(receipt.buyerId)
                    .collection(Constants.PAYMENTRECEIPT)
                    .document(receiptRef.id)

                transaction.set(buyerRef, BuyerSellerLedgerModel(
                    date = receipt.paymentDate,
                    invoiceNumber = receipt.invoiceNumber,
                    totalAmount = receipt.totalAmount,
                    type = Constants.PAYMENTRECEIPT
                ))

                //This will subtract the new amount to the buyer id total amount in ledger
                amount = totalAmount - receipt.totalAmount!!
                transaction.set(ledgerRef, CreditorDebitorModel(name = receipt.buyerName, totalAmount=amount))

                //this will add the invoice id to the ledger to that buyerID
                val ledgerInvoiceRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.DEBTORS)
                    .document(receipt.buyerId)
                    .collection(Constants.PAYMENTRECEIPT)
                    .document(receiptRef.id)

                transaction.set(ledgerInvoiceRef, hashMapOf<String, Any>())

            }
        }

    }

    override suspend fun getALlPaymentReceipt(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PAYMENTRECEIPT).get().await()
        collection
    }

    override fun deletePaymentReceipt(receiptDocumentId: String, buyerId: String) {

        val receiptRef =  firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PAYMENTRECEIPT)
            .document(receiptDocumentId)

        val ledgerPaymentReceiptRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS)
            .document(buyerId)
            .collection(Constants.PAYMENTRECEIPT)
            .document(receiptRef.id)

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS)
            .document(buyerId)

        val buyerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BUYER)
            .document(buyerId)
            .collection(Constants.PAYMENTRECEIPT)
            .document(receiptDocumentId)


        firestore.runTransaction { transaction->

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            val currentPaymentReceiptValue = transaction.get(receiptRef).getDouble("totalAmount") ?: 0.0

            val amount = totalAmount + currentPaymentReceiptValue

            transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

            transaction.delete(receiptRef)
            transaction.delete(ledgerPaymentReceiptRef)
            transaction.delete(buyerRef)
        }
    }

    override suspend fun getAllInvoices(buyerId: String): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BUYER)
            .document(buyerId)
            .collection(Constants.INVOICE)
            .get().await()
        collection
    }
}