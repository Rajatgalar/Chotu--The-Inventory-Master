package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ExpiryModel
import com.itechnowizard.chotu.domain.model.ProductModel

interface ExpiryRepository {

    suspend fun getAllProduct(): List<ExpiryModel>

}