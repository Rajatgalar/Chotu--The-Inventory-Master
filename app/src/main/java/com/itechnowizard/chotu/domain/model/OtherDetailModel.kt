package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OtherDetailModel(
    val challanNumber : String? = "",
    val ewayBillNumber : String? = "",
    val freightCharge : String? = "",
    val freightChargeAmount : String? = "",
    val freightChargeGst : String? = "",
    val insuranceCharge : String? = "",
    val insuranceChargeAmount : String? = "",
    val insuranceChargeGst : String? = "",
    val loadingCharge : String? = "",
    val loadingChargeAmount : String? = "",
    val loadingChargeGst : String? = "",
    val otherCharge : String? = "",
    val otherChargeAmount : String? = "",
    val otherChargeGst : String? = "",
    val otherChargeName : String? = "",
    val packagingCharge : String? = "",
    val packagingChargeAmount : String? = "",
    val packagingChargeGst : String? = "",
    val podate : String? = "",
    val ponumber : String ?= "",
    var reverseCharge : String?="",
    val salesPerson : String? = "",
    val taxPref : String? = "",
    var tcs : String ?= "",
    val tcsType : String? = "",
) : Parcelable