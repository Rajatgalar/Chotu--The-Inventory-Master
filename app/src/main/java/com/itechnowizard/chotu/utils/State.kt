package com.itechnowizard.chotu.utils

import com.google.firebase.firestore.QuerySnapshot

data class State<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String = ""
)
