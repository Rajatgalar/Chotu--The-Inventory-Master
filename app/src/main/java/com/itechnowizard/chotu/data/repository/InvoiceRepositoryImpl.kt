package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.BuyerSellerLedgerModel
import com.itechnowizard.chotu.domain.model.InventoryModel
import com.itechnowizard.chotu.domain.model.InvoiceModel
import com.itechnowizard.chotu.domain.model.CreditorDebitorModel
import com.itechnowizard.chotu.domain.repository.InvoiceRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InvoiceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : InvoiceRepository {

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

        val imageRef = storage.reference.child("$userId/invoice/$invoiceCode")

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

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS)
            .document(invoice.buyerId!!)

        firestore.runTransaction { transaction ->
            // Update the inventory quantities

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            var amount = 0.0

            //
            invoice.productDetails!!.withIndex().forEach { (index,product) ->
                val productRef = firestore.collection(userId)
                    .document(Constants.INVENTORY)
                    .collection(product.itemId!!)

                if(product.inventoryId!!.isNotEmpty()){ ////
                    val docId = product.inventoryId!!
                    val inventoryDocRef = productRef.document(docId)
                    transaction.set(inventoryDocRef,  InventoryModel(date = invoice.invoiceDate,
                        stock = product.quantity!!.toInt() * -1,
                        productId = product.itemId,
                    remark = "Sale",
                    sno = invoice.invoiceNumber))
                }else{//create new
                    val newInventoryDocRef = productRef.document()
                    transaction.set(newInventoryDocRef,  InventoryModel(date = invoice.invoiceDate,
                        stock = product.quantity!!.toInt() * -1,
                        productId = product.itemId,
                        remark = "Sale",
                        sno = invoice.invoiceNumber))
                    invoice.productDetails!![index].inventoryId= newInventoryDocRef.id
                }
            }

            // Add the invoice document
            if (forUpdate) {
                val invoiceRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.INVOICE)
                    .document(invoiceDocId)
                transaction.set(invoiceRef, invoice)

                if(previousBillFinalAmount != invoice.billFinalAmount){

                    amount = if(previousBillFinalAmount>invoice.billFinalAmount!!)
                        totalAmount - previousBillFinalAmount-invoice.billFinalAmount!!
                    else
                        totalAmount + invoice.billFinalAmount!! - previousBillFinalAmount

                    transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

                    val buyerRef = firestore.collection(userId)
                        .document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.BUYER)
                        .document(invoice.buyerId)
                        .collection(Constants.INVOICE)
                        .document(invoiceDocId)

                    transaction.set(buyerRef, BuyerSellerLedgerModel(
                        date = invoice.invoiceDate,
                        invoiceNumber = invoice.invoiceNumber,
                        totalAmount = invoice.billFinalAmount,
                        type = Constants.INVOICE
                    ))

                } else {
                    return@runTransaction
                }

            } else { // if the invoice is new then new invoice should be created
                val invoiceRef =
                    firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.INVOICE)
                        .document()
                transaction.set(invoiceRef, invoice)

                //Below we are creating buyerRef to the invoices linked with this buyer and adding the model
                val buyerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.BUYER)
                    .document(invoice.buyerId)
                    .collection(Constants.INVOICE)
                    .document(invoiceRef.id)

                transaction.set(buyerRef, BuyerSellerLedgerModel(
                    date = invoice.invoiceDate,
                    invoiceNumber = invoice.invoiceNumber,
                    totalAmount = invoice.billFinalAmount,
                    type = Constants.INVOICE
                ))

                //This will add the new amount to the buyer id total amount in ledger
                amount = totalAmount + invoice.billFinalAmount!!
                transaction.set(ledgerRef, CreditorDebitorModel(name = invoice.buyerDetail!!.companyName,totalAmount= amount))

                //this will add the invoice id to the ledger to that buyerID
                val ledgerInvoiceRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.DEBTORS)
                    .document(invoice.buyerId)
                    .collection(Constants.INVOICE)
                    .document(invoiceRef.id)

                transaction.set(ledgerInvoiceRef, hashMapOf<String, Any>())

            }
        }

    }

    override suspend fun getALlInvoice(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.INVOICE).get().await()
        collection
    }

    override fun deleteInvoice(invoiceDocId: String, buyerId: String) {

        val invoiceRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.INVOICE)
            .document(invoiceDocId)

        val ledgerInvoiceRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS)
            .document(buyerId)
            .collection(Constants.INVOICE)
            .document(invoiceRef.id)

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.DEBTORS)
            .document(buyerId)

        val buyerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BUYER)
            .document(buyerId)
            .collection(Constants.INVOICE)
            .document(invoiceDocId)

        firestore.runTransaction { transaction->

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            val currentInvoiceValue = transaction.get(invoiceRef).getDouble("billFinalAmount") ?: 0.0
            var amount = totalAmount - currentInvoiceValue

            transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

            transaction.delete(invoiceRef)
            transaction.delete(ledgerInvoiceRef)
            transaction.delete(buyerRef)
        }
    }
}