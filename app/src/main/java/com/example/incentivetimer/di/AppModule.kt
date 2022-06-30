package com.example.incentivetimer.di

import android.app.Application
import androidx.room.Room
import com.example.incentivetimer.data.ITDatabase
import com.example.incentivetimer.data.RewardDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //we don't need to make dao as singleton it's already singleton created by room
    @Provides
    fun provideRewardDao(db: ITDatabase): RewardDao = db.rewardDao()

    @Singleton
    @Provides
    fun provideRoomDatabase(
        app: Application,
        callback: ITDatabase.Callback
    ): ITDatabase = Room.databaseBuilder(app, ITDatabase::class.java, "it_database")
        .addCallback(callback)
        .build()

    @ApplicationScope
    @Singleton
    @Provides
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope