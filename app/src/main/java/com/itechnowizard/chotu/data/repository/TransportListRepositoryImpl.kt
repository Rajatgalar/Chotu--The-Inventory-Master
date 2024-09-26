package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.TransportListModel
import com.itechnowizard.chotu.domain.repository.TransportListRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TransportListRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : TransportListRepository{

    private var userId: String = auth.currentUser!!.uid

    override fun addTransport(transportListModel: TransportListModel, isForUpdate : Boolean, documentId : String) {
        if(isForUpdate){
            firestore.collection(userId)
                .document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.TRANSPORT_LIST)
                .document(documentId)
                .set(transportListModel)
        }else {
            firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
                .collection(Constants.TRANSPORT_LIST)
                .add(transportListModel)
        }
    }

    override suspend fun getALlTransport(): QuerySnapshot = withContext(Dispatchers.IO) {
        val collection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.TRANSPORT_LIST).get().await()
        collection
    }

    override fun deleteTransport(docId: String) {
        firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.TRANSPORT_LIST)
            .document(docId)
            .delete()
    }
}