package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SupplierModel(
    val address : String ?= "",
    val city : String? = "",
    val email : String? = "",
    val firmName : String? = "",
    val gstin : String? = "",
    var imageUrl : String?="",
    var mobile : String ?= "",
    val panCard : String? = "",
    val pincode : String?="",
    val state : String?=""
) : Parcelable