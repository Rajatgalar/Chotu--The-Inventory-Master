package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.model.SupplierModel

interface SupplierRepository {

    fun addSupplierDetails(imageUri: Uri?,supplier : SupplierModel)

    suspend fun getSupplierDetails(): SupplierModel?

}