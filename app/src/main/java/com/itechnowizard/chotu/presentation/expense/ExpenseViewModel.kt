package com.itechnowizard.chotu.presentation.expense

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.usecase.AddExpenseUseCase
import com.itechnowizard.chotu.domain.usecase.GetAllExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getAllExpenseUseCase: GetAllExpenseUseCase
) : ViewModel() {

    private val _expenseList = MutableLiveData<List<ExpenseModel>>()
    val expenseList: LiveData<List<ExpenseModel>> = _expenseList

    fun saveExpenseToFirebase(accountType: String, amount: String, category: String,
                              dateOfExpense: String, imageUrl: String, note: String,
                              paymentMode: String, spendFor: String)
    {
        val expense = ExpenseModel(accountType, amount, category, dateOfExpense, imageUrl, note, paymentMode, spendFor)
        addExpenseUseCase.execute(expense)
    }

    fun loadAllExpense() {
        viewModelScope.launch {
            _expenseList.value = getAllExpenseUseCase()
        }
    }

    fun saveExpenseToFirebaseWithImage(imageUri: Uri,accountType: String, amount: String, category: String,
                              dateOfExpense: String, imageUrl: String, note: String,
                              paymentMode: String, spendFor: String)
    {
        val expense = ExpenseModel(accountType, amount, category, dateOfExpense, imageUrl, note, paymentMode, spendFor)
        addExpenseUseCase.executeWithImage(imageUri,expense)
    }

}