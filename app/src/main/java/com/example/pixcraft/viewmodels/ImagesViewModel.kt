package com.example.pixcraft.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixcraft.models.PixCraftModel
import com.example.pixcraft.repository.PixCraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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
}