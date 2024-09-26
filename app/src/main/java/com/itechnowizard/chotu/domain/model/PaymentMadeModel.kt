package com.itechnowizard.chotu.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMadeModel(
    val accountType: String? = "",
    val paymentDate: String? = "",
    val paymentMode: String? = "",
    val purchaseLink: String? = "",
    val purchaseNumber: String? = "",
    val receiptNumber: String? = "1",
    val remarks: String? = "",
    val sellerId: String? = "",
    val sellerName: String? = "",
    var supplierDetail : SupplierModel?= null,
    val totalAmount: Double? = 0.0,
    val treatment: String? = "",
) : Parcelable