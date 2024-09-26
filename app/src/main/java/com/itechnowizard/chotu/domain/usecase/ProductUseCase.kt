package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.repository.ProductRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    fun sendData(product: ProductModel,isForUpdate : Boolean, documentId : String) : Resource<Unit> {
        return try{
            productRepository.addProduct(product,isForUpdate,documentId)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchProductList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = productRepository.getAllProduct()
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(itemName: String) : Resource<Unit> {
        return try {
            productRepository.deleteProduct(itemName)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

