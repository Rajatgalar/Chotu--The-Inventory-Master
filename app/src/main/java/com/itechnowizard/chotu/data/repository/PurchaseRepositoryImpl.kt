package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.domain.repository.PurchaseRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PurchaseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : PurchaseRepository{

    private var userId: String = auth.currentUser!!.uid

    override fun addPurchase(
        purchase: PurchaseModel,
        isForUpdate: Boolean,
        documentId: String,
        bitmapByteData: ByteArray?,
        previousBillFinalAmount: Double
    ) {
        if(bitmapByteData != null){
            sendImageToDatabase(bitmapByteData,purchase,isForUpdate,documentId, previousBillFinalAmount)
        }else{
            sendData(purchase,isForUpdate,documentId, previousBillFinalAmount)
        }
    }

    private fun sendImageToDatabase(
        bitmapByteData: ByteArray,
        purchase: PurchaseModel,
        forUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double
    ) {

        // Create a reference to the image in Firebase Storage
        //PurchaseCode keeps the
        val purchaseCode = purchase.purchaseCode?.takeIf { it.isNotBlank() } ?: "$userId-${System.currentTimeMillis()}.jpg"

        val imageRef = storage.reference.child("$userId/Purchase/$purchaseCode")

        // Upload the image to Firebase Storage
        val uploadTask = imageRef.putBytes(bitmapByteData)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) { task.exception?.let { throw it } }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                purchase.imageUrl = downloadUrl
                purchase.purchaseCode = purchaseCode
//                Constants.DOWNLOAD_URL = downloadUrl
                sendData(purchase, forUpdate, documentId, previousBillFinalAmount)
            }
        }
    }

    private fun sendData(
        purchase: PurchaseModel,
        forUpdate: Boolean,
        documentId: String,
        previousBillFinalAmount: Double
    ) {

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR)
            .document(purchase.sellerId!!)

        firestore.runTransaction { transaction ->
            // Update the inventory quantities

            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            var amount = 0.0


            purchase.productDetails!!.withIndex().forEach { (index,product) ->
                val productRef = firestore.collection(userId)
                    .document(Constants.INVENTORY)
                    .collection(product.itemId!!)
                if(product.inventoryId!!.isNotEmpty()){ ////
                    val docId = product.inventoryId!!
                    val inventoryDocRef = productRef.document(docId)
                    transaction.set(inventoryDocRef,  InventoryModel(date = purchase.purchaseDate,
                        stock = product.quantity!!.toInt() * -1,
                        productId = product.itemId,
                        remark = "Add",
                        sno = purchase.purchaseNumber))
                }else{//create new
                    val newInventoryDocRef = productRef.document()
                    transaction.set(newInventoryDocRef,  InventoryModel(date = purchase.purchaseDate,
                        stock = product.quantity!!.toInt(),
                        productId = product.itemId,
                        remark = "Add",
                        sno = purchase.purchaseNumber))
                    purchase.productDetails!![index].inventoryId= newInventoryDocRef.id
                }
            }

            // Add the purchase document
            if (forUpdate) {
                val purchaseRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.PURCHASE)
                    .document(documentId)
                transaction.set(purchaseRef, purchase)

                if(previousBillFinalAmount != purchase.billFinalAmount){

                    amount = if(previousBillFinalAmount>purchase.billFinalAmount!!){
                        totalAmount - previousBillFinalAmount-purchase.billFinalAmount!!
                    }else{
                        totalAmount + purchase.billFinalAmount!! - previousBillFinalAmount
                    }

                    transaction.update(ledgerRef,Constants.FIREBASE_TOTAL_AMOUNT,amount)

                    //here we will udpate the seller also
                    val sellerRef = firestore.collection(userId)
                        .document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.SELLER)
                        .document(purchase.sellerId)
                        .collection(Constants.PURCHASE)
                        .document(documentId)

                    transaction.set(sellerRef, BuyerSellerLedgerModel(
                        date = purchase.purchaseDate,
                        invoiceNumber  = purchase.purchaseNumber,
                        totalAmount = purchase.billFinalAmount,
                        type = Constants.PURCHASE
                    )
                    )


                } else {
                    return@runTransaction
                }

            } else {
                val purchaseRef =
                    firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                        .collection(Constants.PURCHASE)
                        .document()
                transaction.set(purchaseRef, purchase)

                //Below we are creating buyerRef to the invoices linked with this buyer and adding the model
                val sellerRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_DOCUMENT_LISTS)
                    .collection(Constants.SELLER)
                    .document(purchase.sellerId)
                    .collection(Constants.PURCHASE)
                    .document(purchaseRef.id)

                transaction.set(sellerRef, BuyerSellerLedgerModel(
                    date = purchase.purchaseDate,
                    invoiceNumber  = purchase.purchaseNumber,
                    totalAmount = purchase.billFinalAmount,
                    type = Constants.PURCHASE
                )
                )

                //This will add the new amount to the seller id total amount in ledger
                amount = totalAmount + purchase.billFinalAmount!!
                transaction.set(ledgerRef, CreditorDebitorModel(name = purchase.sellerDetail!!.companyName, totalAmount=amount))

                val ledgerInvoiceRef = firestore.collection(userId)
                    .document(Constants.FIREBASE_LEDGER)
                    .collection(Constants.CREDITOR)
                    .document(purchase.sellerId)
                    .collection(Constants.PURCHASE)
                    .document(purchaseRef.id)

                transaction.set(ledgerInvoiceRef, hashMapOf<String, Any>())

            }
        }
    }


    override suspend fun getALLPurchase(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PURCHASE).get().await()
        collection
    }

    override fun deletePurchase(docId: String, sellerId: String) {

        val purchaseRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PURCHASE)
            .document(docId)

        val ledgerPurchaseRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR)
            .document(sellerId)
            .collection(Constants.PURCHASE)
            .document(purchaseRef.id)

        val ledgerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_LEDGER)
            .collection(Constants.CREDITOR)
            .document(sellerId)

        val sellerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SELLER)
            .document(sellerId)
            .collection(Constants.PURCHASE)
            .document(purchaseRef.id)


        firestore.runTransaction { transaction->
            val totalAmount = transaction.get(ledgerRef).getDouble("totalAmount") ?: 0.0
            val currentInvoiceValue = transaction.get(purchaseRef).getDouble("billFinalAmount") ?: 0.0
            var amount = totalAmount - currentInvoiceValue

            transaction.update(ledgerRef, Constants.FIREBASE_TOTAL_AMOUNT, amount)

            transaction.delete(purchaseRef)
            transaction.delete(ledgerPurchaseRef)
            transaction.delete(sellerRef)
        }
    }
}