package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HighLowStockModel(
    val highStockName : String? = "",
    val highStockValue : Int? = 0,
    val lowStockName : String? = "",
    val lowStockValue : Int? = 0,
) : Parcelable