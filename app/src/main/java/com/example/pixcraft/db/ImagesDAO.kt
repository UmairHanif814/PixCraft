package com.example.pixcraft.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pixcraft.models.ImagesModel

@Dao
interface ImagesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertImages(image: ImagesModel): Long

    @Query("Select * from images_table")
    suspend fun getImages(): List<ImagesModel>

    @Query("Select count(imagePath) from images_table where imagePath=:imagePath")
    suspend fun isImageExistInDb(imagePath:String):Int
}