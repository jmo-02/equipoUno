package com.example.miniproyyecto1.di


import com.example.miniproyecto1.repository.InventoryRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideInventoryRepository(
        firestore: FirebaseFirestore
    ): InventoryRepository {
        return InventoryRepository(firestore)
    }
}
