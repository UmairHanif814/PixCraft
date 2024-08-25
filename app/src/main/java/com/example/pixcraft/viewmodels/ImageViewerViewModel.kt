package com.example.pixcraft.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.pixcraft.models.ImagesModel
import com.example.pixcraft.models.Src
import com.example.pixcraft.repository.PixCraftRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel @Inject constructor(
    private val repository: PixCraftRepository,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext context: Context
) :
    ViewModel() {
    private val _imageSrc = MutableStateFlow<Src?>(null)
    val imageSrc: StateFlow<Src?> get() = _imageSrc

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    val isImageExistsInDb: StateFlow<Boolean> get() = repository.isImageExistsInDb

    init {
        viewModelScope.launch {
            val encodedSrcJson = savedStateHandle.get<String>("imageSrc") ?: ""
            val srcJson = URLDecoder.decode(encodedSrcJson, "UTF-8")
            val src = Gson().fromJson(srcJson, Src::class.java)
            _imageSrc.emit(src)
            val fileName = "image_${extractImageName(src.original)}.jpg"
            val directory = File(context.cacheDir, "PixCraft Images")
            val file = File(directory, fileName)
            isImageExistsInDB(file.absolutePath)
        }
    }

    private fun isImageExistsInDB(imagePath: String) {
        viewModelScope.launch {
            repository.isImageExistsInDB(imagePath)
        }
    }

    fun saveImage(imageUrl: String?, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            val fileName = "image_${extractImageName(imageUrl)}.jpg"
            val directory = File(context.cacheDir, "PixCraft Images")

            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)

            try {
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()

                saveBitmapToFile(bitmap, file)
                repository.insetImage(
                    ImagesModel(
                        imagePath = file.absolutePath
                    )
                )
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                _loading.value = false
                repository.updateIsImageExists()

            }
        }
    }

    private fun extractImageName(url: String?): String {
        return url?.substringAfterLast("/", "default_image")
            ?.substringBeforeLast(".") ?: ""
    }

    private fun saveBitmapToFile(bitmap: Bitmap, file: File) {
        try {
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}