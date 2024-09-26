package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.SellerModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel

interface SellerRepository {


    fun addSeller(seller : SellerModel,isForUpdate : Boolean, documentId : String)

    suspend fun getALlSeller() : QuerySnapshot

    fun deleteSeller(docId : String)
}