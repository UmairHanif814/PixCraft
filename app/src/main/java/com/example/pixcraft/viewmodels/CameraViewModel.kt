package com.example.pixcraft.viewmodels

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixcraft.models.ImagesModel
import com.example.pixcraft.repository.PixCraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: PixCraftRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    fun saveImageToLocalStorage(context: Context, imageUri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "captured_image_${System.currentTimeMillis()}.jpg"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val imageUriResult = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val imageOutStream = imageUriResult?.let { uri ->
                resolver.openOutputStream(uri)
            }

            imageOutStream?.use { outputStream ->
                val inputStream = context.contentResolver.openInputStream(imageUri)
                inputStream?.copyTo(outputStream)

                // Get the file path using the content resolver
                val filePath = getFilePathFromUri(resolver, imageUriResult)

                // Insert image details into the repository
                if (filePath != null) {
                    repository.insetImage(
                        ImagesModel(
                            imagePath = filePath
                        )
                    )
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFilePathFromUri(resolver: ContentResolver, uri: Uri?): String? {
        if (uri == null) return null

        val cursor = resolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }
}
