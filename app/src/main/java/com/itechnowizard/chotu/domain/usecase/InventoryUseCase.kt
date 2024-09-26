package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.InventoryModel
import com.itechnowizard.chotu.domain.model.ProductInventory
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.repository.InventoryRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class InventoryUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    fun sendData(inventory: InventoryModel,isForUpdate : Boolean, documentId : String) : Resource<Unit>{
        return try{
            inventoryRepository.addInventory(inventory,isForUpdate,documentId)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun getallProduct() : Flow<Resource<List<ProductModel>>> = flow {
            try {
                emit(Resource.Loading())
                val productList = inventoryRepository.getAllInventoryAndItem()
                emit(Resource.Success(productList))
            } catch (e: Exception) {
                emit(Resource.Error(e.message))
            }
        }

    fun deleteDocument(docId: String) : Resource<Unit>{
        return try {
            inventoryRepository.deleteInventory(docId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

