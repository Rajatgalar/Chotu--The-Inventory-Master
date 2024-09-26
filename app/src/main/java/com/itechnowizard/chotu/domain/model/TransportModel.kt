package com.itechnowizard.chotu.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransportModel(
    val dateOfSupply: String? = "",
    val documentNumber: String? = "",
    val placeOfSupply: String? = "",
    var supplyType: String?="",
    val transportId :String?="",
    val transportName :String?="",
    val transportationMode: String ?= "",
    val vehicleNumber: String? = ""
) : Parcelable