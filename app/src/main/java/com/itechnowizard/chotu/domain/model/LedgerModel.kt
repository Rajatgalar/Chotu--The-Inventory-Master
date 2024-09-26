package com.itechnowizard.chotu.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LedgerModel(
    var creditorList : List<CreditorDebitorModel>? = null,
    var debitorList : List<CreditorDebitorModel>? = null
) : Parcelable
