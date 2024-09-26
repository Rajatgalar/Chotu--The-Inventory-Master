package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransportListModel(
    var transportId: String? = "",
    var name: String?=""
) : Parcelable