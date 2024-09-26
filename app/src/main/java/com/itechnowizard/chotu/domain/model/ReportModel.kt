package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ReportModel(
    val highLowStockModel: HighLowStockModel? = null,
    val totalClient : Int? = 0,
    val totalPurchase : Double? = 0.0,
    val totalSell : Double? = 0.0,
    val totalVendor : Int? = 0,
) : Parcelable