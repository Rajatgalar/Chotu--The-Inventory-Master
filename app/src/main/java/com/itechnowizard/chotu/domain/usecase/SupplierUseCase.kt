package com.itechnowizard.chotu.domain.usecase

import android.net.Uri
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.domain.repository.SupplierRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SupplierUseCase @Inject constructor(
    private val supplierRepository: SupplierRepository
) {
    fun execute(imageUri: Uri?,supplier: SupplierModel) : Resource<Unit>{
        return try{
            supplierRepository.addSupplierDetails(imageUri,supplier)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }


    fun fetchDetails(): Flow<Resource<SupplierModel?>> = flow {
        try {
            emit(Resource.Loading())
            val supplierDetails = supplierRepository.getSupplierDetails()
            emit(Resource.Success(supplierDetails))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }
}
