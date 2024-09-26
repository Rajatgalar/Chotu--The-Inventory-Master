package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.BankModel
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel

interface BankRepository {


    fun addBank(buyer : BankModel,isForUpdate : Boolean, documentId : String)

    suspend fun getALlBank() : QuerySnapshot

    fun deleteBank(docId : String)
}