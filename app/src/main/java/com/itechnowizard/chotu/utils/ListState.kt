package com.itechnowizard.chotu.utils

import com.google.firebase.firestore.QuerySnapshot

data class ListState<T>(
    val isLoading: Boolean = false,
    val list: QuerySnapshot? = null,
    val error: String = ""
)
