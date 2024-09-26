package com.itechnowizard.chotu.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BuyerSellerLedgerModel(
    val date : String? = "",
    var id : String? = "",
    val invoiceNumber : String? = "",
    val totalAmount : Double? = 0.0,
    val type : String? = "",
) : Parcelable