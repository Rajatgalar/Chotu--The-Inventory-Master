package com.itechnowizard.chotu.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CreditNoteModel(
    var amount : String?="",
    var billFinalAmount: Double? = 00.00,
    val buyerDetail : BuyerModel?= null,
    val buyerId : String?= "",
    var cess : String?="",
    var consigneeDetailType : String?= "",
    var consigneeModel: ConsigneeModel?= null,
    var gst : String?="",
    var imageUrl : String? = "",
    var creditNoteCode : String? = "",
    val creditNoteDate : String? = "",
    val creditNoteNumber : String? = "",
    var productDetails : List<SelectedProductModel>? = null,
    var supplierDetail : SupplierModel?= null,
    var taxableAmount : String?="",
    var termsAndCondition : String? = "",
    var totalAmount : String?="",
    var totalTax : String?="",
    var transportDetails: TransportModel?= null
) : Parcelable
