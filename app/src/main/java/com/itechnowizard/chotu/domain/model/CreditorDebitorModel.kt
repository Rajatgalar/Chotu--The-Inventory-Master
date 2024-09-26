package com.itechnowizard.chotu.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreditorDebitorModel(
    var id : String? = "",
    val name : String? = "",
    val totalAmount : Double? = 0.0,
) : Parcelable
