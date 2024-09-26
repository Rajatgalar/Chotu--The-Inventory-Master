package com.itechnowizard.chotu.data.repository

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.ContactModel
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.domain.repository.ContactRepository
import com.itechnowizard.chotu.domain.repository.BuyerRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : ContactRepository{

    private var userId: String = auth.currentUser!!.uid

    override fun addContact(buyer: ContactModel, isForUpdate : Boolean, documentId : String) {
        if(isForUpdate){
            firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.CONTACT)
                .document(documentId)
                .set(buyer)
        }else {
            firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.CONTACT)
                .add(buyer)
        }
    }

    override suspend fun getALlContact(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.CONTACT).get().await()
        collection
    }

    override fun deleteContact(docId: String) {
        firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.CONTACT)
            .document(docId)
            .delete()
    }
}