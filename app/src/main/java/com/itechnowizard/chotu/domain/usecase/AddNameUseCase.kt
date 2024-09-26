package com.itechnowizard.chotu.domain.usecase

import com.itechnowizard.chotu.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddNameUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository
) {
    fun execute(name: String): Boolean {
        return expenseRepository.addNewUser(name)
    }
}
