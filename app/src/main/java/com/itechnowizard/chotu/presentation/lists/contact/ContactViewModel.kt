package com.itechnowizard.chotu.presentation.lists.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.ContactModel
import com.itechnowizard.chotu.domain.model.BuyerModel
import com.itechnowizard.chotu.domain.usecase.ContactUseCase
import com.itechnowizard.chotu.utils.ListState
import com.itechnowizard.chotu.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(
    private val contactUseCase: ContactUseCase
) : ViewModel() {
    private val _addContactResult = MutableLiveData<Resource<Unit>>()
    val addContactResult: LiveData<Resource<Unit>> = _addContactResult

    private val _removeContactResult = MutableLiveData<Resource<Unit>>()
    val removeContactResult: LiveData<Resource<Unit>> = _removeContactResult

    private val _contactListState = MutableLiveData<ListState<QuerySnapshot>>()
    val contactListState: LiveData<ListState<QuerySnapshot>> = _contactListState

    fun addContact(contact: ContactModel,isForUpdate : Boolean, documentId : String) {
        val result = contactUseCase.sendData(contact,isForUpdate,documentId)
        _addContactResult.value = result
    }

    fun deleteContactDocument(docId: String){
        val result = contactUseCase.deleteDocument(docId)
        _removeContactResult.value = result
    }

    fun loadContactList() {
        contactUseCase.fetchContactList().onEach { result->
            when(result){
                is Resource.Success ->{
                    _contactListState.value = ListState(list = result.data)
                }
                is Resource.Error ->{
                    _contactListState.value = ListState(error = result.message ?: "Unexpected error occurs")
                }
                is Resource.Loading ->{
                    _contactListState.value =ListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }


}