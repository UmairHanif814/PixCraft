package com.example.pixcraft.repository

import com.example.pixcraft.api.PixCraftApi
import com.example.pixcraft.db.ImagesDAO
import com.example.pixcraft.db.ImagesDB
import com.example.pixcraft.models.ImagesModel
import com.example.pixcraft.models.PixCraftModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PixCraftRepository @Inject constructor(private val pixCraftApi: PixCraftApi,private val imagesDAO: ImagesDAO) {
    private val _images=MutableStateFlow<PixCraftModel?>(null)
    val images:StateFlow<PixCraftModel?> get() = _images

    private val _isImageExists=MutableStateFlow(false)
    val isImageExistsInDb:StateFlow<Boolean> get() = _isImageExists

    suspend fun getImages(){
        val response=pixCraftApi.getImages()
        if (response.isSuccessful && response.body()!=null){
            _images.emit(response.body())
        }
    }

    suspend fun getSearchedImages(query:String){
        _images.emit(null)
        val response=pixCraftApi.getSearchedImages(query)
        if (response.isSuccessful && response.body()!=null){
            _images.emit(response.body())
        }
    }

    suspend fun insetImage(images: ImagesModel){
        imagesDAO.insertImages(images)
    }

    suspend fun isImageExistsInDB(imagePath:String){
        val response=imagesDAO.isImageExistInDb(imagePath)
        if (response==0){
            _isImageExists.emit(false)
        }else{
            _isImageExists.emit(true)
        }
    }

    suspend fun updateIsImageExists(){
        _isImageExists.emit(true)
    }
}