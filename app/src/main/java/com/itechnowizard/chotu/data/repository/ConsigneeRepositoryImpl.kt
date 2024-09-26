package com.itechnowizard.chotu.data.repository

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.ConsigneeModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.domain.repository.BuyerRepository
import com.itechnowizard.chotu.domain.repository.ConsigneeRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ConsigneeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : ConsigneeRepository{

    private var userId: String = auth.currentUser!!.uid

    override fun addBuyer(buyer: ConsigneeModel, isForUpdate : Boolean, documentId : String) {
        if(isForUpdate){
            firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.CONSIGNEE)
                .document(documentId)
                .set(buyer)
        }else {
            firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.CONSIGNEE)
                .add(buyer)
        }
    }

    override suspend fun getALlBuyer(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.CONSIGNEE).get().await()
        collection
    }

    override fun deleteBuyer(docId: String) {
        firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.CONSIGNEE)
            .document(docId)
            .delete()
    }
}