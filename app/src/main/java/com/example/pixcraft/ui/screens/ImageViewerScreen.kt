package com.example.pixcraft.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.example.pixcraft.viewmodels.ImageViewerViewModel
import com.example.pixcraft.viewmodels.ImagesViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageViewerScreen(){
    val imageViewerViewModel:ImageViewerViewModel= hiltViewModel()
    val imageSrc=imageViewerViewModel.imageSrc.collectAsState()
    GlideImage(
        model = imageSrc.value?.original,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(1f),
        contentScale = ContentScale.Crop,
        loading = placeholder{
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LottiePlaceholder()
            }
        }
    )
}