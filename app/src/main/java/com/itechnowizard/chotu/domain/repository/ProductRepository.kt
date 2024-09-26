package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ProductModel

interface ProductRepository {

    fun addProduct(product: ProductModel,isForUpdate : Boolean, documentId : String)

    suspend fun getAllProduct(): QuerySnapshot

    fun deleteProduct(itemName : String)

}