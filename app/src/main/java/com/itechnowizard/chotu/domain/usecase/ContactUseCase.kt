package com.itechnowizard.chotu.domain.usecase

import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ContactModel
import com.itechnowizard.chotu.domain.repository.ContactRepository
import com.itechnowizard.chotu.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ContactUseCase @Inject constructor(
    private val contactRepository: ContactRepository
) {
    fun sendData(buyer: ContactModel,isForUpdate : Boolean, documentId : String) : Resource<Unit>{
        return try{
            contactRepository.addContact(buyer,isForUpdate,documentId)
            Resource.Success(Unit)
        }catch (e: Exception) {
            Resource.Error(e.message)
        }
    }

    fun fetchContactList(): Flow<Resource<QuerySnapshot>> = flow{
        try {
            emit(Resource.Loading())
            val contact = contactRepository.getALlContact()
            emit(Resource.Success(contact))
        } catch (e:Exception){
            emit(Resource.Error(e.message))
        }
    }

    fun deleteDocument(docId: String) : Resource<Unit>{
        return try {
            contactRepository.deleteContact(docId)
            Resource.Success(Unit)
        }catch (e : Exception){
            Resource.Error(e.message)
        }
    }

}

