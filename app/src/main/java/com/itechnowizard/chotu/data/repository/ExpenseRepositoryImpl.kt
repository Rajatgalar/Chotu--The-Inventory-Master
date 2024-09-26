package com.itechnowizard.chotu.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.domain.model.ExpenseModel
import com.itechnowizard.chotu.domain.repository.ExpenseRepository
import com.itechnowizard.chotu.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ExpenseRepository {
    private lateinit var userId: String

    private fun getUId() {
        userId = auth.currentUser!!.uid
    }

    override fun addNewUser(name: String): Boolean {
        getUId()
        val data = hashMapOf("name" to name)
        firestore
            .collection(userId)
            .document(Constants.FIREBASE_DOCUMENT_PERSONAL_DETAILS)
            .set(data)
            .addOnSuccessListener {
                return@addOnSuccessListener
            }
            .addOnFailureListener {
                Constants.ERROR_MESSAGE = it.localizedMessage?.toString() ?: ""
                return@addOnFailureListener
            }

        return false
    }

    override fun addExpense(myExpense: ExpenseModel) {
        //getUId()
        firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.EXPENSE)
            .add(myExpense)

    }

    override fun addExpenseWithImage(imageUri: Uri, expense: ExpenseModel) {

        // Generate a unique filename for the image
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${userId}_$timeStamp.jpg"

        // Create a reference to the image in Firebase Storage
        val imageRef = storage.reference.child("$userId/expenses/$fileName")

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
                expense.imageUrl = downloadUrl

                addExpense(expense)
            }
        }
    }

    override suspend fun getAllExpense(): List<ExpenseModel> = withContext(Dispatchers.IO) {
        getUId()
        val documents = firestore.collection(userId).document(Constants.FIREBASE_DOCUMENT_LISTS)
            .collection(Constants.EXPENSE).get().await()
        documents.toObjects(ExpenseModel::class.java)
    }

}