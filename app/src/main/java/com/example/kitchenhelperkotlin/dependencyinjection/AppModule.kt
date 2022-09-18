package com.example.kitchenhelperkotlin.dependencyinjection

import android.app.Application
import androidx.room.Room
import com.example.kitchenhelperkotlin.KHDatabase
import com.example.kitchenhelperkotlin.util.MIGRATION_1_2
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

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: KHDatabase.Callback
    ) = Room.databaseBuilder(app, KHDatabase::class.java, "KH_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        //.addMigrations(MIGRATION_1_2) Добавление миграции бд
        .build()

    @Provides
    fun provideToBuyDao(db: KHDatabase) = db.toBuyDao()

    @Provides
    fun provideProductDao(db: KHDatabase) = db.ProductDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope