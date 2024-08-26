package com.example.pixcraft.viewmodels

import android.app.WallpaperManager
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.example.pixcraft.models.Src
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class SavedImageViewerViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle):ViewModel() {
    private val _imageSrc = MutableStateFlow<String?>(null)
    val imageSrc: StateFlow<String?> get() = _imageSrc

    private val _wallpaperStatus = MutableStateFlow<String?>(null)
    val wallpaperStatus: StateFlow<String?> get() = _wallpaperStatus

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    init {
        viewModelScope.launch {
            val encodedSrcJson = savedStateHandle.get<String>("image") ?: ""
            val srcJson = URLDecoder.decode(encodedSrcJson, "UTF-8")
            val src = Gson().fromJson(srcJson, String::class.java)
            _imageSrc.emit(src)
        }
    }

    fun setAsWallpaper(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value=true
            try {
                val imageUrl = _imageSrc.value
                if (imageUrl != null) {
                    // Download the image using Glide
                    val bitmap = Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .submit()
                        .get()

                    // Get the WallpaperManager
                    val wallpaperManager = WallpaperManager.getInstance(context)

                    // Set the bitmap as the wallpaper
                    wallpaperManager.setBitmap(bitmap)

                    // Update the wallpaper status
                    _wallpaperStatus.emit("Wallpaper set successfully")
                }
            } catch (e: Exception) {
                _wallpaperStatus.emit("Failed to set wallpaper: ${e.message}")
            } finally {
                _loading.value=false
            }
        }
    }
}