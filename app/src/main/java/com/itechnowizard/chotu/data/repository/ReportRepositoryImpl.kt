package com.itechnowizard.chotu.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.itechnowizard.chotu.domain.model.*
import com.itechnowizard.chotu.domain.repository.LedgerRepository
import com.itechnowizard.chotu.domain.repository.ReportRepository
import com.itechnowizard.chotu.domain.repository.TransportListRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.Continuation

class ReportRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth
) : ReportRepository {

    private var userId: String = auth.currentUser!!.uid

    override suspend fun getReport(): ReportModel = withContext(Dispatchers.IO) {

        val client = getTotalClients()
        val vendor = getTotalVendor()
        val sell = getTotalSell()
        val purchase = getTotalPurchase()
        val highLowStockModel = getHighLowstock()


        // Create a LedgerModel object with the merged lists
        ReportModel(totalClient = client, totalVendor = vendor, totalPurchase = purchase,
        totalSell = sell, highLowStockModel = highLowStockModel)
    }

    private suspend fun getTotalClients(): Int{
        val buyerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.BUYER)
        val snapshot = buyerRef.get().await()
        return snapshot?.size() ?: 0
    }

    private suspend fun getTotalVendor(): Int{
        val sellerRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SELLER)
        val snapshot = sellerRef.get().await()
        return snapshot?.size() ?: 0
    }


    private suspend fun getTotalSell(): Double {
        val invoiceCollection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.INVOICE).get().await()

        var totalSold = 0.0

        invoiceCollection.documents.forEach { invoiceDoc ->
            val inventoryModel = invoiceDoc.toObject(InvoiceModel::class.java)
            if (isWithinLast30Days(inventoryModel!!.invoiceDate)) {
                totalSold += inventoryModel.billFinalAmount!!
            }
        }

        return totalSold
    }

    private suspend fun getTotalPurchase(): Double {
        val invoiceCollection = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PURCHASE).get().await()

        var totalSold = 0.0

        invoiceCollection.documents.forEach { invoiceDoc ->
            val inventoryModel = invoiceDoc.toObject(PurchaseModel::class.java)
            if (isWithinLast30Days(inventoryModel!!.purchaseDate)) {
                totalSold += inventoryModel.billFinalAmount!!
            }
        }

        return totalSold
    }


    private fun isWithinLast30Days( date : String?): Boolean{
        // Get the timestamp for 30 days ago
        val timeZone = TimeZone.getTimeZone("Asia/Kolkata")
        val calendar = Calendar.getInstance(timeZone)
        calendar.add(Calendar.DAY_OF_MONTH, -30)
        val thirtyDaysAgo = calendar.timeInMillis
        val inventoryDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(
            date ?: ""
        )?.time ?: 0
        return inventoryDate >= thirtyDaysAgo

    }

    private suspend fun getHighLowstock(): HighLowStockModel {
        //This contains the product list Ids
        val productDocumentRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PRODUCT).get().await()

        // Initialize the maps to keep track of the total stock sold for each product
        val soldMap = mutableMapOf<String, Int>()

        //looping in each product and getting the sell list
        for(product in productDocumentRef) {

            var sellList : MutableList<InventoryModel> = mutableListOf()
            val inventoryCollection = firestore.collection(userId)
                .document(Constants.INVENTORY)
                .collection(product.id).get().await()

            //seperate the list of add and sale
            inventoryCollection.documents.forEach { inventoryDoc ->
                val inventoryModel = inventoryDoc.toObject(InventoryModel::class.java)
                if (inventoryDoc.getString("remark") == "Sale") {
                    sellList.add(inventoryModel!!)
                }
            }

// Calculate the total stock sold for each product in the sellList
            for (inventory in sellList) {
                val productId = inventory.productId ?: continue
                val stockSold = kotlin.math.abs(inventory.stock ?: 0)

                if (isWithinLast30Days(inventory.date)) {
                    soldMap[productId] = (soldMap[productId] ?: 0) + stockSold
                }
            }
        }

        // Sort the soldMap by value in descending order
        val sortedSoldMap = soldMap.toList().sortedByDescending { (_, value) -> value }.toMap()

// Get the highest and lowest sold products
        val highestSoldProduct = sortedSoldMap.keys.firstOrNull()
        val lowestSoldProduct = sortedSoldMap.keys.lastOrNull()

        println(" In Reprot id = highsold = $highestSoldProduct and its value is ${soldMap[highestSoldProduct]}")
        val highProductNameRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PRODUCT).document(highestSoldProduct!!)

        val lowProductNameRef = firestore.collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.PRODUCT).document(lowestSoldProduct!!)

        val highProductName = highProductNameRef.get().await().getString("itemName")
        val lowProductName = lowProductNameRef.get().await().getString("itemName")

       return HighLowStockModel(highStockName = highProductName,
            highStockValue = soldMap[highestSoldProduct],
            lowStockName = lowProductName,
            lowStockValue = soldMap[lowestSoldProduct],)
    }


}