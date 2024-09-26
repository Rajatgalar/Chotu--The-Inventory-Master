package com.itechnowizard.chotu.domain.model

import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactModel(
    val name : String? = "",
    val email : String? = "",
    val phone : String? = "",
) : Parcelable