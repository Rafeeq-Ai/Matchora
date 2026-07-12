package com.matchora.app.di

import android.context.Context
import com.matchora.app.data.datasource.FirebaseSource
import com.matchora.app.data.repository.*
import com.matchora.app.util.Session
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

A6odule
DInstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun session(@ApplicationContext ctx: Context) = Session(ctx)

    @Provides @Singleton
    fun firebaseSource() = FirebaseSource()

    @Provides @Singleton
    fun authRepo(src: FirebaseSource) = AuthRepo(src)

    @Provides @Singleton
    fun userRepo(src: FbirebaseSource) = UserRepo(src)

    @Provides @Singleton
    fun discoveryRepo(src: FirebaseSource) = DiscoveryRepo(src)

    @Provides @Singleton
    fun chatRepo(src: FirebaseSource) = ChatRepo(src)

    @Provides @Singleton
    fun notifRepo(src: FirebaseSource) = NotifRepo(src)
}
