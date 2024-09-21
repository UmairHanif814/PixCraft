package com.example.pixcraft.repository

import android.content.Context
import com.example.pixcraft.api.PixCraftApi
import com.example.pixcraft.db.ImagesDAO
import com.example.pixcraft.models.ImagesModel
import com.example.pixcraft.models.MediaStoreImagesModel
import com.example.pixcraft.models.PixCraftModel
import com.example.pixcraft.utils.MediaStoreHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PixCraftRepository @Inject constructor(
    private val pixCraftApi: PixCraftApi,
    private val imagesDAO: ImagesDAO,
    @ApplicationContext private val context: Context
) {
    private val _images = MutableStateFlow<PixCraftModel?>(null)
    val images: StateFlow<PixCraftModel?> get() = _images

    private val _galleryImages = MutableStateFlow<List<MediaStoreImagesModel>>(emptyList())
    val galleryImages: StateFlow<List<MediaStoreImagesModel>> get() = _galleryImages

    private val _savedImages = MutableStateFlow<List<ImagesModel>>(emptyList())
    val savedImages: StateFlow<List<ImagesModel>> get() = _savedImages

    private val _isImageExists = MutableStateFlow(false)
    val isImageExistsInDb: StateFlow<Boolean> get() = _isImageExists

    suspend fun getImages() {
        val response = pixCraftApi.getImages()
        if (response.isSuccessful && response.body() != null) {
            _images.emit(response.body())
        }
    }

    suspend fun getGalleryImages() {
        val response = MediaStoreHelper.getAllImages(context)
        if (response.isNotEmpty()) {
            _galleryImages.emit(response)
        }
    }

    suspend fun getSearchedImages(query: String) {
        _images.emit(null)
        val response = pixCraftApi.getSearchedImages(query)
        if (response.isSuccessful && response.body() != null) {
            _images.emit(response.body())
        }
    }

    suspend fun insetImage(images: ImagesModel) {
        imagesDAO.insertImages(images)
    }

    suspend fun isImageExistsInDB(imagePath: String) {
        val response = imagesDAO.isImageExistInDb(imagePath)
        if (response == 0) {
            _isImageExists.emit(false)
        } else {
            _isImageExists.emit(true)
        }
    }

    suspend fun updateIsImageExists() {
        _isImageExists.emit(true)
    }

    suspend fun getSavedImages() {
        val response = imagesDAO.getImages()
        if (response.isNotEmpty()) {
            _savedImages.emit(response)
        }
    }
}