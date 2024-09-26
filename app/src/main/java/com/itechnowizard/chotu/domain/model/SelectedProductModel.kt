package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SelectedProductModel(

    val amount: Double? = 00.00,
    val cess: String? = "",
    var cessType: String?="",
    val cessValue: Double? = 0.0,
    val discount: String ?= "",
    val discountType: String? = "",
    val discountValue: Double? = 0.0,
    val gstPercentType: String? = "",
    val gstValue: Double? = 0.0,
    val hsn: String? = "",
    var inventoryId: String ?= "",
    val itemId: String ?= "",
    val itemName: String ?= "",
    val quantity: String? = "0",
    var unitPrice: String?="",
    val taxInclusiveExclusiveType: String? = "1",
    val taxableAmount: Double? = 00.00,
    var totalAmount: Double ?= 00.00,
    var unitType: String ?= "",
) : Parcelable