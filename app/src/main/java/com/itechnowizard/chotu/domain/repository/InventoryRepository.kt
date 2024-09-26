package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.InventoryModel
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.model.SupplierModel

interface InventoryRepository {


    fun addInventory(inventory : InventoryModel,isForUpdate : Boolean, documentId : String)

    suspend fun getAllInventoryAndItem() : List<ProductModel>

    fun deleteInventory(docId : String)
}