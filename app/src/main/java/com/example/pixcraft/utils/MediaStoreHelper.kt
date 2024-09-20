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
    suspend fun getAllVideoFiles(
        context: Context,
        result: ((
            list: ArrayList<MediaStoreImagesModel>,
        ) -> Unit)? = null,
    ) {
        measureTime {
            var cursor: Cursor? = null
            try {
                val list = ArrayList<MediaStoreImagesModel>()
                list.clear()

                val selection = MediaStore.Video.Media.DURATION + "> 0"

                val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }

                //For Android 10 and Android 11
                cursor = (context.contentResolver.query(
                    uri,
                    projectionColumVideos, selection,
                    null,
                    MediaStore.Video.Media.DATE_ADDED + " ASC",
                ) ?: return)

                cursor.moveToFirst()


                while (!cursor.isAfterLast) {
                    if (!coroutineContext.isActive) {
                        break
                    }
                    val model = MediaStoreImagesModel()

                    model.title =
                        cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))

                    model.imagePath =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA))

                    val urix: Uri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                    )
                    model.uri = urix.toString()

                    list.add(model)

                    cursor.moveToNext()
                }

                withContext(Dispatchers.Main) {
                    result?.invoke(
                        list
                    )
                }


            } catch (e: Exception) {
                e.message?.printIt()
            } finally {
                cursor?.close()
            }
        }.apply {
            "t-->$this".printIt()
        }
    }

    val projectionColumVideos =
        arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DATA,
        )

}