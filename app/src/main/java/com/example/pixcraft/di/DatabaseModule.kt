package com.example.pixcraft.di

import android.content.Context
import androidx.room.Room
import com.example.pixcraft.db.ImagesDAO
import com.example.pixcraft.db.ImagesDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

class DatabaseModule {
    @Provides
    @Singleton
    fun providesImagesDB(@ApplicationContext appContext: Context): ImagesDB {
        return Room.databaseBuilder(appContext, ImagesDB::class.java, "ImagesDB").build()
    }

    @Singleton
    @Provides
    fun provideYourDao(database: ImagesDB): ImagesDAO {
        return database.getImagesDao()
    }
}