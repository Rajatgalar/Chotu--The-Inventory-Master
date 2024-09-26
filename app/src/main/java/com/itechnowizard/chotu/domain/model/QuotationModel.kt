package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.itechnowizard.chotu.presentation.lists.bank.Bank
import kotlinx.android.parcel.Parcelize

@Parcelize
data class QuotationModel(
    var amount : String?="",
    var billFinalAmount: Double? = 00.00,
    val buyerDetail : BuyerModel?= null,
    val buyerId : String?= "",
    var cess : String?="",
    var consigneeDetailType : String?= "",
    var consigneeModel: ConsigneeModel?= null,
    var contactDetails: ContactModel?= null,
    var gst : String?="",
    var imageUrl : String? = "",
    var invoiceCode : String? = "",
    val invoiceDate : String? = "",
    val invoiceNumber : String? = "",
    val invoicePrefix : String? = "",
    var optionalDetails : String? = "",
    var otherDetails: OtherDetailModel?= null,
    val paidAmount : Double? = 0.0,
    var productDetails : List<SelectedProductModel>? = null,
    val quotationValidity : String? = "",
    var supplierDetail : SupplierModel?= null,
    var taxableAmount : String?="",
    var termsAndCondition : String? = "",
    var totalAmount : String?="",
    var totalTax : String?="",
) : Parcelable
