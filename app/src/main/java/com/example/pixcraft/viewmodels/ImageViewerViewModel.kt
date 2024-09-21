package com.example.pixcraft.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.pixcraft.models.ImagesModel
import com.example.pixcraft.models.MediaStoreImagesModel
import com.example.pixcraft.models.Photo
import com.example.pixcraft.repository.PixCraftRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
) : ViewModel() {

    private val _imageSrc = MutableStateFlow<List<Photo>>(emptyList())
    val imageSrc: StateFlow<List<Photo>> get() = _imageSrc

    private val _galleryImageSrc = MutableStateFlow<List<MediaStoreImagesModel>>(emptyList())
    val galleryImageSrc: StateFlow<List<MediaStoreImagesModel>> get() = _galleryImageSrc

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    private val _progress = MutableStateFlow<Pair<Int,Int>>(Pair(0,0))
    val progress: StateFlow<Pair<Int,Int>> get() = _progress

    val isImageExistsInDb: StateFlow<Boolean> get() = repository.isImageExistsInDb

    private val _initialPage = MutableStateFlow(0)
    val initialPage: StateFlow<Int> get() = _initialPage

    init {
        viewModelScope.launch {
            val encodedSrcJson = savedStateHandle.get<String>("imageSrc") ?: ""
            val srcJson = URLDecoder.decode(encodedSrcJson, "UTF-8")
            val srcListType = object : TypeToken<List<Photo>>() {}.type
            val srcList: List<Photo> = Gson().fromJson(srcJson, srcListType)
            _imageSrc.emit(srcList)


            val gallerySrcListType = object : TypeToken<List<MediaStoreImagesModel>>() {}.type
            val gallerySrcList: List<MediaStoreImagesModel> = Gson().fromJson(srcJson, gallerySrcListType)
            _galleryImageSrc.emit(gallerySrcList)


            val initialPageIndex = savedStateHandle.get<Int>("initialPage") ?: 0
            _initialPage.emit(initialPageIndex)


//            val fileName = "image_${extractImageName(srcList[initialPageIndex].s
        //            rc.original)}.jpg"
//            val directory = File(context.cacheDir, "PixCraft Images")
//            val file = File(directory, fileName)
//            isImageExistsInDB(file.absolutePath)
        }
    }

    private fun isImageExistsInDB(imagePath: String) {
        viewModelScope.launch {
            repository.isImageExistsInDB(imagePath)
        }
    }
    // Add a cancellation flag
    private val _isCancelled = MutableStateFlow(false)

    fun saveAllImages(context: Context,isComingFromGallery:Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value = true
            _isCancelled.value = false  // Reset the cancellation flag

            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "PixCraft Images")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val images = if (isComingFromGallery){
                galleryImageSrc.value
            }else{
                imageSrc.value
            }
            var savedCount = 0
            _progress.value = Pair(savedCount, images.size)

            for (image in images) {
                if (_isCancelled.value) {
                    break
                }

                try {
                    val imageUrl = when (image) {
                        is MediaStoreImagesModel -> image.imagePath
                        is Photo -> image.src.original
                        else -> continue
                    }
                    val fileName = "image_${extractImageName(imageUrl)}.jpg"
                    val file = File(directory, fileName)

                    if (!file.exists()) {
                        val bitmap = Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .submit()
                            .get()

                        saveBitmapToFile(bitmap, file)
                        repository.insetImage(ImagesModel(imagePath = file.absolutePath))
                    }

                    savedCount++
                    _progress.value = Pair(savedCount, images.size)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            _loading.value = false
        }
    }

    // Add a method to handle cancellation
    fun cancelSaveOperation() {
        _isCancelled.value = true
        _loading.value = false
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
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)
                out.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
