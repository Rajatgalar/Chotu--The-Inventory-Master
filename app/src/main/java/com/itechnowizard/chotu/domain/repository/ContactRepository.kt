package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ContactModel
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel

interface ContactRepository {


    fun addContact(buyer : ContactModel,isForUpdate : Boolean, documentId : String)

    suspend fun getALlContact() : QuerySnapshot

    fun deleteContact(docId : String)
}