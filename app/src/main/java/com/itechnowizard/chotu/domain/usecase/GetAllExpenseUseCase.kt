package com.itechnowizard.chotu.domain.usecase

import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetAllExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(): List<ExpenseModel> = repository.getAllExpense()
}
