package com.itechnowizard.chotu.domain.model

data class ExpenseModel(
    val accountType : String? = "",
    val amount : String? = "",
    val category : String? = "",
    val dateOfExpense : String? = "",
    var imageUrl : String ?= "",
    val note : String ?= "",
    val paymentMode : String? = "",
    val spendFor : String?=""
)