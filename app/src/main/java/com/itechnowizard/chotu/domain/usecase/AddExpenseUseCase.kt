package com.itechnowizard.chotu.domain.usecase

import android.net.Uri
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.repository.ExpenseRepository
import javax.inject.Inject


class AddExpenseUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {

    fun execute(expense: ExpenseModel) {
        expenseRepository.addExpense(expense)
    }

    fun executeWithImage(imageUri: Uri,expense: ExpenseModel) {
        expenseRepository.addExpenseWithImage(imageUri,expense)
    }

}
