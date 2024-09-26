package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ConsigneeModel(
    val address : String? = "",
    var city : String?="",
    val companyName : String? = "",
    val email : String? = "",
    val gstin : String ?= "",
    val gsttreatmentType : String? = "",
    val mobile : String? = "",
    val pincode : String? = "",
    var state : String ?= ""
) : Parcelable{
    constructor(buyer: BuyerModel) : this(
        address = buyer.address,
        city = buyer.city,
        companyName = buyer.companyName,
        email = buyer.email,
        gstin = buyer.gstin,
        gsttreatmentType = buyer.gsttreatmentType,
        mobile = buyer.mobile,
        pincode = buyer.pincode,
        state = buyer.state
    )

    constructor(seller: SellerModel) : this(
        address = seller.address,
        city = seller.city,
        companyName = seller.companyName,
        email = seller.email,
        gstin = seller.gstin,
        gsttreatmentType = seller.gsttreatmentType,
        mobile = seller.mobile,
        pincode = seller.pincode,
        state = seller.state
    )
}