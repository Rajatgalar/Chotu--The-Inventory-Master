package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ExpiryModel
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.model.PurchaseModel
import com.itechnowizard.chotu.domain.repository.ExpiryRepository
import com.itechnowizard.chotu.domain.repository.ProductRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExpiryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : ExpiryRepository {

    private var userId: String = auth.currentUser!!.uid

    override suspend fun getAllProduct(): List<ExpiryModel> = withContext(Dispatchers.IO) {

        val listOfExpiryModel : MutableList<ExpiryModel>  = mutableListOf()

        val productCollection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PRODUCT).get().await()

        productCollection.documents.forEach { invoiceDoc ->
            val productModel = invoiceDoc.toObject(ProductModel::class.java)
            listOfExpiryModel.add(
                ExpiryModel(
                    expiryDate= productModel!!.expiryDate,
                    productId = invoiceDoc.id,
                    productName = productModel.itemName))
        }

        listOfExpiryModel
    }

}