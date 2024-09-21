package com.example.pixcraft.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixcraft.models.MediaStoreImagesModel
import com.example.pixcraft.models.PixCraftModel
import com.example.pixcraft.repository.PixCraftRepository
import com.example.pixcraft.utils.isNetworkAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryImagesViewModel @Inject constructor(
    private val repository: PixCraftRepository,
    @ApplicationContext context: Context
) : ViewModel() {
    val images: StateFlow<List<MediaStoreImagesModel>> get() = repository.galleryImages

    private val _networkAvailable = mutableStateOf(true)
    val isNetworkAvailable get() = _networkAvailable

    init {
        loadImages(context)
    }

    private fun loadImages(context: Context) {
        viewModelScope.launch {
            if (context.isNetworkAvailable()) {
                _networkAvailable.value = true
                repository.getGalleryImages()
            } else {
                _networkAvailable.value = false
            }
        }
    }

    fun getGalleryImages(context: Context) {
        viewModelScope.launch {
            if (context.isNetworkAvailable()) {
                _networkAvailable.value = true
                repository.getGalleryImages()
            } else {
                _networkAvailable.value = false
            }
        }
    }

    fun checkNetworkState(context: Context) {
        viewModelScope.launch {
            loadImages(context)
        }
    }
}
