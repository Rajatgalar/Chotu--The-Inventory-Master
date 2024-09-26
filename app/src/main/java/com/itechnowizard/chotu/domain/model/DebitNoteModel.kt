package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.itechnowizard.chotu.presentation.lists.bank.Bank
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DebitNoteModel(
    var amount : String?="",
    var billFinalAmount: Double? = 00.00,
    var cess : String?="",
    var consigneeDetailType : String?= "",
    var consigneeModel: ConsigneeModel?= null,
    var gst : String?="",
    var imageUrl : String? = "",
    var debitNoteCode : String? = "",
    val debitNoteDate : String? = "",
    val debitNoteNumber : String? = "",
    var productDetails : List<SelectedProductModel>? = null,
    val sellerDetail : SellerModel?= null,
    val sellerId : String?= "",
    var supplierDetail : SupplierModel?= null,
    var taxableAmount : String?="",
    var termsAndCondition : String? = "",
    var totalAmount : String?="",
    var totalTax : String?="",
    var transportDetails: TransportModel?= null
) : Parcelable
