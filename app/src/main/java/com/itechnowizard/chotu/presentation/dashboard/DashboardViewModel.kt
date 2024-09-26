package com.itechnowizard.chotu.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itechnowizard.chotu.domain.usecase.AddNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val addNameUseCase: AddNameUseCase
) : ViewModel() {

    private val _isNameSaved = MutableLiveData<Boolean>()
    val isNameSaved: LiveData<Boolean>
        get() = _isNameSaved

    fun saveNameToFirebase(name:String) {
        _isNameSaved.value = addNameUseCase.execute(name)
    }
}