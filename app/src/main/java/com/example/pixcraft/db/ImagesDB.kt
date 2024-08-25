package com.example.pixcraft.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pixcraft.models.ImagesModel

@Database(entities = [ImagesModel::class], version = 1)
abstract class ImagesDB:RoomDatabase() {
    abstract fun getImagesDao():ImagesDAO
}