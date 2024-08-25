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

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageViewerScreen() {
    val imageViewerViewModel: ImageViewerViewModel = hiltViewModel()
    val imageSrc = imageViewerViewModel.imageSrc.collectAsState()
    val context = LocalContext.current

    // The image takes the full size of the screen
    GlideImage(
        model = imageSrc.value?.original,
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
    DownloadButton(imageViewerViewModel, imageSrc, context)
    LoadingIndicator(imageViewerViewModel)
}

@Composable
fun DownloadButton(
    imageViewerViewModel: ImageViewerViewModel,
    imageSrc: State<Src?>,
    context: Context
) {
    val isExists = imageViewerViewModel.isImageExistsInDb.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            onClick = {
                if (!isExists.value) {
                    imageViewerViewModel.saveImage(imageSrc.value?.original, context)
                }else{
                    Toast.makeText(context, "Already Saved.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = if (isExists.value) {
                            listOf(Color(0xFF42f54b), Color(0xFF016106))
                        } else {
                            listOf(Color(0xFF00B4DB), Color(0xFF0083B0))
                        }
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {

            Text(
                text = if (isExists.value) {
                    "Saved"
                } else {
                    "Download"
                }, color = Color.White, modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun LoadingIndicator(imageViewerViewModel: ImageViewerViewModel) {
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