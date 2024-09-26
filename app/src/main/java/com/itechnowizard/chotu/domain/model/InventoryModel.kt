package com.itechnowizard.chotu.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InventoryModel(
    val date : String? = "",
    var productId : String? = "",
    var remark : String? = "",
    var sno : String?="1",
    var stock : Int?=0,
) : Parcelable