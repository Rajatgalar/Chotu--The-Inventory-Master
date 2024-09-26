package com.itechnowizard.chotu.data.repository

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.BankModel
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.domain.repository.BankRepository
import com.itechnowizard.chotu.domain.repository.BuyerRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BankRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : BankRepository{

    private var userId: String = auth.currentUser!!.uid

    override fun addBank(buyer: BankModel, isForUpdate : Boolean, documentId : String) {
        if(isForUpdate){
            firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.BANK)
                .document(documentId)
                .set(buyer)
        }else {
            firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.BANK)
                .add(buyer)
        }
    }

    override suspend fun getALlBank(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BANK).get().await()
        collection
    }

    override fun deleteBank(docId: String) {
        firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BANK)
            .document(docId)
            .delete()
    }
}