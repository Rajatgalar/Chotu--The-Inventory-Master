package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.itechnowizard.chotu.domain.model.ExpenseModel

interface ExpenseRepository {

    fun addExpense(myExpense: ExpenseModel)
    suspend fun getAllExpense(): List<ExpenseModel>
    fun addNewUser(name : String) : Boolean
    fun addExpenseWithImage(imageUri: Uri, expense: ExpenseModel)
}