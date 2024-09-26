package com.itechnowizard.chotu.utils


data class ListStateAny<T>(
    val isLoading: Boolean = false,
    val list: List<T>? = null,
    val error: String = ""
)
