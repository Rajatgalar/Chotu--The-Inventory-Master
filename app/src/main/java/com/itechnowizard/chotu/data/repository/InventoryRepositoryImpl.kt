package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.InventoryModel
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.repository.InventoryRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : InventoryRepository{

    private var userId: String = auth.currentUser!!.uid

    override fun addInventory(inventory: InventoryModel,isForUpdate : Boolean, documentId : String) {
        if(isForUpdate){
            firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.BUYER)
                .document(documentId)
                .set(inventory)
        }else {
            firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.BUYER)
                .add(inventory)
        }
    }


    override suspend fun getAllInventoryAndItem(): List<ProductModel> = withContext(Dispatchers.IO) {
        // Retrieve all products
        val productCollection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PRODUCT).get().await()

        // Associate inventory information with each product
        productCollection.documents.mapNotNull { productDoc ->
            val productId = productDoc.id
            val product = productDoc.toObject(ProductModel::class.java)
            if (product != null) {
                // Retrieve inventory documents for this product
                val inventoryCollection = firestore.collection(userId)
                    .document(Constants.INVENTORY)
                    .collection(productId).get().await()

                // Associate the inventory information with the product
                product.inventory = inventoryCollection.documents.mapNotNull { inventoryDoc ->
                    inventoryDoc.toObject(InventoryModel::class.java)
                }
                product
            } else {
                null
            }
        }
    }


    override fun deleteInventory(docId: String) {
        firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BUYER)
            .document(docId)
            .delete()
    }
}