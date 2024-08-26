package com.example.pixcraft.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pixcraft.models.ImagesModel
import com.example.pixcraft.viewmodels.SavedViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SavedImagesScreen(onItemClick:(String)->Unit){
    val savedImagesViewModel:SavedViewModel= hiltViewModel()
    val savedImages: State<List<ImagesModel>> = savedImagesViewModel.savedImaged.collectAsState()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        items(savedImages.value) {
            SavedImageCard(image = it.imagePath){
                onItemClick(it)
            }

        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SavedImageCard(image:String,onItemClick:(String)->Unit){
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, Color(0xFFEEEEEE))
            .clickable { onItemClick(image) }
        ,
        contentAlignment = Alignment.BottomCenter
    ) {
        GlideImage(
            model = image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            contentScale = ContentScale.Crop,
//            loading = placeholder {
//                LottiePlaceholder()
//            }
        )
    }
}