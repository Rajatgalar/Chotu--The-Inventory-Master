package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.domain.model.TransportListModel

interface TransportListRepository {


    fun addTransport(transportListModel: TransportListModel,isForUpdate : Boolean, documentId : String)

    suspend fun getALlTransport() : QuerySnapshot

    fun deleteTransport(docId : String)
}