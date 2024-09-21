package com.example.pixcraft.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.pixcraft.models.MediaStoreImagesModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

object MediaStoreHelper {

    @OptIn(ExperimentalTime::class)
    @SuppressLint("Range")
    suspend fun getAllImages(context: Context): List<MediaStoreImagesModel> {
        var cursor: Cursor? = null
        val list = ArrayList<MediaStoreImagesModel>()

        try {
            val selection = MediaStore.Images.Media.DURATION + "> 0"

            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            cursor = context.contentResolver.query(
                uri,
                projectionColumVideos, null,
                null,
                MediaStore.Images.Media.DATE_ADDED + " ASC"
            ) ?: return emptyList()

            cursor.moveToFirst()

            while (!cursor.isAfterLast) {
//                if (!coroutineContext.isActive) {
//                    break
//                }
                val model = MediaStoreImagesModel()

                model.title = cursor.getString(
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)
                )

                model.imagePath = cursor.getString(
                    cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                )

                val urix: Uri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                )
                model.uri = urix.toString()

                list.add(model)
                cursor.moveToNext()
            }
        } catch (e: Exception) {
            e.message?.printIt()
        } finally {
            cursor?.close()
        }


        return list
    }


    val projectionColumVideos =
        arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DATA,
        )

}