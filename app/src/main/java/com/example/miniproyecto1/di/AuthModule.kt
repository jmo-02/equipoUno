package com.example.miniproyecto1.di // <--- 1. Corregido el "yy" a "y"

import com.example.miniproyecto1.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    // 2. ELIMINADO: fun provideFirebaseAuth...
    // Ya no es necesaria porque "FirebaseModule" ya se encarga de esto.

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth // Hilt tomará esto automáticamente de FirebaseModule
    ): AuthRepository = AuthRepository(auth)
}
