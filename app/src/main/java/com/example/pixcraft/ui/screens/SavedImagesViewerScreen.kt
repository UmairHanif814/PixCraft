package com.example.pixcraft.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.pixcraft.models.Src
import com.example.pixcraft.viewmodels.ImageViewerViewModel
import com.example.pixcraft.viewmodels.SavedImageViewerViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SavedImagesViewerScreen() {
    val imageViewerViewModel: SavedImageViewerViewModel = hiltViewModel()
    val imageSrc = imageViewerViewModel.imageSrc.collectAsState()
    val context = LocalContext.current

    // The image takes the full size of the screen
    GlideImage(
        model = imageSrc.value,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        requestBuilderTransform = {
            it.diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        },
        loading = placeholder {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LottiePlaceholder()
            }
        }
    )
    SetAsWallpaperButton(imageViewerViewModel,context)
    LoadingIndicatorView(imageViewerViewModel)
}
@Composable
fun SetAsWallpaperButton(
    imageViewerViewModel: SavedImageViewerViewModel,
    context: Context
) {
    val wallpaperStatus by imageViewerViewModel.wallpaperStatus.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Button(
            onClick = {
                imageViewerViewModel.setAsWallpaper(context)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF00B4DB), Color(0xFF0083B0))
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {
            Text(
                text = "Set as Wallpaper", color = Color.White, modifier = Modifier.padding(16.dp)
            )
        }

        if (wallpaperStatus != null) {
            Toast.makeText(context, wallpaperStatus, Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun LoadingIndicatorView(imageViewerViewModel: SavedImageViewerViewModel) {
    val isLoading = imageViewerViewModel.loading.collectAsState()

    if (isLoading.value) {

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LottiePlaceholder()
        }
    }
}
