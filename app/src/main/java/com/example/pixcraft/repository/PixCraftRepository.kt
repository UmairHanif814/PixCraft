package com.example.pixcraft.repository

import com.example.pixcraft.api.PixCraftApi
import com.example.pixcraft.models.PixCraftModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PixCraftRepository @Inject constructor(private val pixCraftApi: PixCraftApi) {
    private val _images=MutableStateFlow<PixCraftModel?>(null)
    val images:StateFlow<PixCraftModel?> get() = _images

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
}