package com.itechnowizard.chotu.domain.repository

import android.net.Uri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.itechnowizard.chotu.domain.model.*

interface LedgerRepository {

    suspend fun getLedger() : LedgerModel

    suspend fun getDetailLedger(buyerId: String, isBuyer: Boolean) : List<BuyerSellerLedgerModel>

}