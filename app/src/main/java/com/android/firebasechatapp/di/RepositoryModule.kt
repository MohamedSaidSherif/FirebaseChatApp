package com.android.firebasechatapp.di

import android.content.Context
import com.android.firebasechatapp.data.repository.AccountSettingRepositoryImp
import com.android.firebasechatapp.data.repository.AuthenticationRepositoryImp
import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideDi() = Dispatchers.IO

    @Singleton
    @Provides
    fun provideAuthenticationRepository(
        firebaseAuth: FirebaseAuth,
        databaseReference: DatabaseReference,
        coroutineDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context
    ): AuthenticationRepository {
        return AuthenticationRepositoryImp(
            firebaseAuth = firebaseAuth,
            databaseReference = databaseReference,
            coroutineDispatcher = coroutineDispatcher,
            context = context
        )
    }

    @Singleton
    @Provides
    fun provideAccountSettingRepository(
        firebaseAuth: FirebaseAuth,
        databaseReference: DatabaseReference,
        storageReference: StorageReference,
        coroutineDispatcher: CoroutineDispatcher,
        @ApplicationContext context: Context
    ): AccountSettingRepository {
        return AccountSettingRepositoryImp(
            firebaseAuth = firebaseAuth,
            databaseReference = databaseReference,
            storageReference = storageReference,
            coroutineDispatcher = coroutineDispatcher,
            context = context
        )
    }
}