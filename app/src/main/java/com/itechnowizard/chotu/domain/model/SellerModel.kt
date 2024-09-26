package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SellerModel(
    val address : String? = "",
    var city : String?="",
    val companyName : String? = "",
    val email : String? = "",
    val gstin : String ?= "",
    val gsttreatmentType : String? = "",
    val mobile : String? = "",
    val pincode : String? = "",
    var state : String ?= ""
) : Parcelable