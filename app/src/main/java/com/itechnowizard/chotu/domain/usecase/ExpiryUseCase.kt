package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ExpiryModel
import com.itechnowizard.chotu.domain.model.ProductModel
import com.itechnowizard.chotu.domain.repository.ExpiryRepository
import com.itechnowizard.chotu.domain.repository.ProductRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ExpiryUseCase @Inject constructor(
    private val productRepository: ExpiryRepository
) {


    fun fetchProductList(): Flow<Resource<List<ExpiryModel>>> = flow{
        try {
            emit(Resource.Loading())
            val buyer = productRepository.getAllProduct()
            emit(Resource.Success(buyer))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }


}

