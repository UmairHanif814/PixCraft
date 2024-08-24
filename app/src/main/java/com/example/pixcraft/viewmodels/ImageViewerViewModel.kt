package com.example.pixcraft.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixcraft.models.Src
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLDecoder
import javax.inject.Inject

class ImageViewerViewModel @Inject constructor(private val savedStateHandle: SavedStateHandle) :
    ViewModel() {
    private val _imageSrc = MutableStateFlow<Src?>(null)
    val imageSrc: StateFlow<Src?> get() = _imageSrc

    init {
        viewModelScope.launch {
            val encodedSrcJson = savedStateHandle.get<String>("imageSrc") ?: ""
            val srcJson = URLDecoder.decode(encodedSrcJson, "UTF-8")
            val src = Gson().fromJson(srcJson, Src::class.java)
            _imageSrc.emit(src)
        }
    }
}