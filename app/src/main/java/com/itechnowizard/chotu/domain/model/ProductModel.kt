package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductModel(

    val cess : String? = "",
    var cessType : String?="",
    var expiryDate : String?="",
    val gstPercentType : String? = "",
    val hsn : String? = "",
    var inventory : List<InventoryModel>? = null,
    val itemName : String ?= "",
    val lowStockAlert : String? = "",
    val mainStock : String? = "1",
    val openingStock : String? = "",
    val openingStockDate : String? = "",
    val purchasePrice : String? = "",
    val purchaseType : String? = "",
    var salePrice : String?="",
    val taxInclusiveExclusiveType : String? = "1",
    var totalStock : Int? = 0,
    var unit : String ?= "",
) : Parcelable


/*

@Parcelize
data class ProductModel(
    val amount : String? = "",
    val cess : String? = "",
    var cessType : String?="",
    val discount : String ?= "",
    val discountType : String? = "",
    val gstPercentType : String? = "",
    val hsn : String? = "",
    val itemDescription : String? = "",
    val itemName : String ?= "",
    val itemType : String? = "1",
    val lowStockAlert : String? = "",
    val mainStock : String? = "1",
    val minWholeSalePrice : String? = "",
    val openingStock : String? = "",
    val openingStockDate : String? = "",
    val purchasePrice : String? = "",
    val purchaseType : String? = "",
    val quantity : String? = "0",
    var salePrice : String?="",
    val taxInclusiveExclusiveType : String? = "1",
    val taxableAmount : String? = "0",
    var totalAmount : String ?= "0",
    var unit : String ?= "",
    val wholeSalePrice : String? = "",
) : Parcelable
 */