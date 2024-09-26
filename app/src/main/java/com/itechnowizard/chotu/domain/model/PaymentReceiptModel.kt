package com.itechnowizard.chotu.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentReceiptModel(
    val accountType: String? = "",
    val buyerId: String? = "",
    val buyerName: String? = "",
    val invoiceLink: String? = "",
    val invoiceNumber: String? = "",
    val paymentDate: String? = "",
    val paymentMode: String? = "",
    val receiptNumber: String? = "1",
    val remarks: String? = "",
    var supplierDetail : SupplierModel?= null,
    val totalAmount: Double? = 0.0,
    val treatment: String? = "",
) : Parcelable