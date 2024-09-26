package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.ConsigneeModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel

interface ConsigneeRepository {


    fun addBuyer(buyer : ConsigneeModel, isForUpdate : Boolean, documentId : String)

    suspend fun getALlBuyer() : QuerySnapshot

    fun deleteBuyer(docId : String)
}