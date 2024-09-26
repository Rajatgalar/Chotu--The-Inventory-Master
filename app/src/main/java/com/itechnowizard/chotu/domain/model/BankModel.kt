package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BankModel(
    val accountHolderName : String? = "",
    val accountNumber : String? = "",
    val bankName : String? = "",
    val branchName : String? = "",
    var ibanNumber : String?="",
    val ifsccode : String ?= "",
    var swiftCode : String ?= "",
) : Parcelable