package com.itechnowizard.chotu.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.data.repository.ExpenseRepositoryImpl
import com.itechnowizard.chotu.domain.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ExpenseModule {

    @Provides
    @Singleton
    fun providesExpenseRepositoy(firestore : FirebaseFirestore,
                                   auth : FirebaseAuth,
                                    storage: FirebaseStorage)
    = ExpenseRepositoryImpl(firestore,auth,storage) as ExpenseRepository
}