package com.example.pixcraft.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixcraft.models.ImagesModel
import com.example.pixcraft.repository.PixCraftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(private val repository: PixCraftRepository) :ViewModel() {
    val savedImaged:StateFlow<List<ImagesModel>> get() = repository.savedImages

    init {
//        viewModelScope.launch {
//            repository.getSavedImages()
//        }
    }
        suspend fun getSavedImages(){
        repository.getSavedImages()
    }
}