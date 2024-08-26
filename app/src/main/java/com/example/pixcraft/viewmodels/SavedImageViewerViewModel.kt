package com.example.pixcraft.viewmodels

import android.app.WallpaperManager
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.example.pixcraft.utils.GlideTransformation
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

@HiltViewModel
class SavedImageViewerViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle):ViewModel() {
    private val _imageSrc = MutableStateFlow<String?>(null)
    val imageSrc: StateFlow<String?> get() = _imageSrc

    private val _wallpaperStatus = mutableStateOf("")
    val wallpaperStatus:State<String> get() = _wallpaperStatus

    fun setWallpaperStateEmpty(){
        _wallpaperStatus.value=""
    }


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

    fun setAsWallpaper(context: Context, which: Int, transformation: BitmapTransformation?) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.value=true
            try {
                val imageUrl = _imageSrc.value
                if (imageUrl != null) {
                    // Download the image using Glide
                    val bitmap = if (transformation!=null){
                        Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .transform(transformation)
                            .submit()
                            .get()
                    }else{
                        Glide.with(context)
                            .asBitmap()
                            .load(imageUrl)
                            .submit()
                            .get()
                    }

                    // Get the WallpaperManager
                    val wallpaperManager = WallpaperManager.getInstance(context)

                    // Set the bitmap as the wallpaper with the specified flag
                    wallpaperManager.setBitmap(bitmap, null, true, which)

                    // Update the wallpaper status
                    _wallpaperStatus.value="Wallpaper set successfully"
                }
            } catch (e: Exception) {
                _wallpaperStatus.value="Failed to set wallpaper: ${e.message}"
            } finally {
                _loading.value=false
            }
        }
    }

    private val _selectedFilter = MutableStateFlow(GlideTransformation.Default)
    val selectedFilter: StateFlow<GlideTransformation> get() = _selectedFilter

    // List of available filters
    val filters = listOf(
        GlideTransformation.Default,
        GlideTransformation.GrayScale,
        GlideTransformation.Sepia,
        GlideTransformation.InvertColors,
        GlideTransformation.BlackWhite,
        GlideTransformation.Contrast,
        GlideTransformation.Cool,
        GlideTransformation.Brightness,
        GlideTransformation.Negative,
        GlideTransformation.Saturation,
        GlideTransformation.Vintage,
        GlideTransformation.Warmth,
    )

    fun updateSelectedFilter(filter: GlideTransformation) {
        _selectedFilter.value = filter
    }
}