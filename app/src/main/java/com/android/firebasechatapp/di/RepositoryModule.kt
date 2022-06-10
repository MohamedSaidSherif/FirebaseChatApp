package com.android.firebasechatapp.di

import com.android.firebasechatapp.data.repository.AccountSettingRepositoryImp
import com.android.firebasechatapp.data.repository.AuthenticationRepositoryImp
import com.android.firebasechatapp.domain.repository.authentication.AccountSettingRepository
import com.android.firebasechatapp.domain.repository.authentication.AuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
        coroutineDispatcher: CoroutineDispatcher
    ): AuthenticationRepository {
        return AuthenticationRepositoryImp(
            firebaseAuth = firebaseAuth,
            coroutineDispatcher = coroutineDispatcher
        )
    }

    @Singleton
    @Provides
    fun provideAccountSettingRepository(
        firebaseAuth: FirebaseAuth,
        coroutineDispatcher: CoroutineDispatcher
    ): AccountSettingRepository {
        return AccountSettingRepositoryImp(
            firebaseAuth = firebaseAuth,
            coroutineDispatcher = coroutineDispatcher
        )
    }
}