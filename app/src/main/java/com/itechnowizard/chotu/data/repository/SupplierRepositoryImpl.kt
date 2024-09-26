package com.itechnowizard.chotu.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.SupplierModel
import com.itechnowizard.chotu.domain.repository.SupplierRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class SupplierRepositoryImpl@Inject constructor(
    private val firestore: FirebaseFirestore,
    auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : SupplierRepository{

     private var userId: String = auth.currentUser!!.uid

    override fun addSupplierDetails(imageUri: Uri?,supplier: SupplierModel) {
         if(imageUri != null){
            sendImageToDatabase(imageUri,supplier)
        }else{
            sendData(supplier)
        }
    }

    private fun sendData(supplier : SupplierModel){
        firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SUPPLIER)
            .document(userId)
            .set(supplier)
    }

    private fun sendImageToDatabase(imageUri: Uri, supplier: SupplierModel) {
        // Generate a unique filename for the image
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${userId}.jpg"

        // Create a reference to the image in Firebase Storage
        val imageRef = storage.reference.child("$userId/supplierDetails/$fileName")

        // Upload the image to Firebase Storage
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUrl = task.result.toString()
                supplier.imageUrl = downloadUrl
                Constants.DOWNLOAD_URL = downloadUrl
                 sendData(supplier)
            }
        }
    }

    override suspend fun getSupplierDetails(): SupplierModel? = withContext(Dispatchers.IO) {

        val documents = firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.SUPPLIER).document(userId).get().await()
        documents.toObject(SupplierModel::class.java)
    }

}

