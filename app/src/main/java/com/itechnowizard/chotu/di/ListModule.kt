package com.itechnowizard.chotu.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.itechnowizard.chotu.data.repository.*
import com.itechnowizard.chotu.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ListModule {

    @Provides
    @Singleton
    fun providesSupplierRepositoy(firestore : FirebaseFirestore,
                                   auth : FirebaseAuth,
                                    storage: FirebaseStorage)
    = SupplierRepositoryImpl(firestore,auth,storage) as SupplierRepository

    @Provides
    @Singleton
    fun providesBuyerRepositoy(firestore : FirebaseFirestore,
                                   auth : FirebaseAuth)
    = BuyerRepositoryImpl(firestore,auth) as BuyerRepository

    @Provides
    @Singleton
    fun providesExpiryRepositoy(firestore : FirebaseFirestore,
                               auth : FirebaseAuth)
            = ExpiryRepositoryImpl(firestore,auth) as ExpiryRepository

    @Provides
    @Singleton
    fun providesContactRepositoy(firestore : FirebaseFirestore,
                               auth : FirebaseAuth)
            = ContactRepositoryImpl(firestore,auth) as ContactRepository


    @Provides
    @Singleton
    fun providesInventoryRepositoy(firestore : FirebaseFirestore,
                               auth : FirebaseAuth)
            = InventoryRepositoryImpl(firestore,auth) as InventoryRepository

    @Provides
    @Singleton
    fun providesSellerRepositoy(firestore : FirebaseFirestore,
                                   auth : FirebaseAuth)
    = SellerRepositoryImpl(firestore,auth) as SellerRepository

    @Provides
    @Singleton
    fun providesConsigneeRepositoy(firestore : FirebaseFirestore,
                               auth : FirebaseAuth)
            = ConsigneeRepositoryImpl(firestore,auth) as ConsigneeRepository

    @Provides
    @Singleton
    fun providesProductRepositoy(firestore : FirebaseFirestore,
                               auth : FirebaseAuth)
            = ProductRepositoryImpl(firestore,auth) as ProductRepository

    @Provides
    @Singleton
    fun providesBankRepositoy(firestore : FirebaseFirestore,
                               auth : FirebaseAuth)
            = BankRepositoryImpl(firestore,auth) as BankRepository

    @Provides
    @Singleton
    fun providesReportRepositoy(firestore : FirebaseFirestore,
                              auth : FirebaseAuth)
            = ReportRepositoryImpl(firestore,auth) as ReportRepository

    @Provides
    @Singleton
    fun providesInvoiceRepositoy(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ) = InvoiceRepositoryImpl(firestore, auth,storage) as InvoiceRepository

    @Provides
    @Singleton
    fun providesQuotationRepositoy(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ) = QuotationRepositoryImpl(firestore, auth,storage) as QuotationRepository

    @Provides
    @Singleton
    fun providesProformaInvoiceRepositoy(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ) = ProformaInvoiceRepositoryImpl(firestore, auth,storage) as ProformaInvoiceRepository

    @Provides
    @Singleton
    fun providesDebitNoteRepositoy(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ) = DebitNoteRepositoryImpl(firestore, auth,storage) as DebitNoteRepository

    @Provides
    @Singleton
    fun providesCreditNoteRepositoy(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ) = CreditNoteRepositoryImpl(firestore, auth,storage) as CreditNoteRepository

    @Provides
    @Singleton
    fun providesPurchaseRepositoy(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ) = PurchaseRepositoryImpl(firestore, auth,storage) as PurchaseRepository

    @Provides
    @Singleton
    fun providesTransportListRepositoy(firestore : FirebaseFirestore,
                               auth : FirebaseAuth)
            = TransportListRepositoryImpl(firestore,auth) as TransportListRepository

    @Provides
    @Singleton
    fun providesPaymentReceiptRepositoy(firestore : FirebaseFirestore,
                                       auth : FirebaseAuth)
            = PaymentReceiptRepositoryImpl(firestore,auth) as PaymentReceiptRepository

    @Provides
    @Singleton
    fun providesPaymentMadeRepositoy(firestore : FirebaseFirestore,
                                        auth : FirebaseAuth)
            = PaymentMadeRepositoryImpl(firestore,auth) as PaymentMadeRepository

    @Provides
    @Singleton
    fun providesLedgerRepositoy(firestore : FirebaseFirestore,
                                     auth : FirebaseAuth)
            = LedgerRepositoryImpl(firestore,auth) as LedgerRepository
}