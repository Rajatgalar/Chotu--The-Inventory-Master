package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.repository.ProductRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : ProductRepository {

    private var userId: String = auth.currentUser!!.uid
    override fun addProduct(product: ProductModel,isForUpdate : Boolean, documentId : String) {
        if(isForUpdate) {
            firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.PRODUCT)
                .document(documentId)
                .set(product)
        }else{
            firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.PRODUCT)
                .add(product)
        }
    }

    override suspend fun getAllProduct(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PRODUCT).get().await()
        collection
    }

    override fun deleteProduct(itemName: String) {
        firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PRODUCT)
            .document(itemName)
            .delete()
    }

}