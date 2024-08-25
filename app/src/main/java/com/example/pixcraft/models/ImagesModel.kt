package com.example.pixcraft.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images_table")
data class ImagesModel(
    @PrimaryKey(autoGenerate = false)
    val imagePath: String,
)
