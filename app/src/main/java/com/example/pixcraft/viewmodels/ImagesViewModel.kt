package com.example.pixcraft.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.pixcraft.models.ImagesModel
import com.example.pixcraft.models.PixCraftModel
import com.example.pixcraft.repository.PixCraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.http.Query
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ImagesViewModel @Inject constructor(private val repository: PixCraftRepository) :
    ViewModel() {
    val images: StateFlow<PixCraftModel?> get() = repository.images

    init {
        viewModelScope.launch {
            repository.getImages()
        }
    }

    fun getSearchedImages(query: String){
        viewModelScope.launch {
            repository.getSearchedImages(query)
        }
    }
}