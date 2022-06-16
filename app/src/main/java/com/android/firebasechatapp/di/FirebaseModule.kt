package com.android.firebasechatapp.di

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Singleton
    @Provides
    fun provideFirebaseAuthentication() = Firebase.auth

    @Singleton
    @Provides
    fun provideFirebaseDatabaseReference() = Firebase.database.reference

    @Singleton
    @Provides
    fun provideFirebaseStorageReference() = Firebase.storage.reference
}