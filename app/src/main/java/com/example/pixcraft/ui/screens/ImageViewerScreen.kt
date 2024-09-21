package com.example.pixcraft.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pixcraft.viewmodels.ImageViewerViewModel

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun ImageViewerScreen(isFromGallery: Boolean) {
    val imageViewerViewModel: ImageViewerViewModel = hiltViewModel()
    val imageSrc = imageViewerViewModel.imageSrc.collectAsState()
    val imageSrcGallery = imageViewerViewModel.galleryImageSrc.collectAsState()
    val initialPage = imageViewerViewModel.initialPage.collectAsState()
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = initialPage.value) {
        if (isFromGallery) {
            imageSrcGallery.value.size
        } else {
            imageSrc.value.size
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen pager
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val imageUrl = if (isFromGallery) {
                imageSrcGallery.value[page].imagePath
            } else {
                imageSrc.value[page].src.original
            }
            GlideImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )
        }

        Text(
            text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
            fontSize = 20.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = MaterialTheme.shapes.medium)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                DownloadButton(
                    imageViewerViewModel,
                    if (isFromGallery) imageSrcGallery.value[pagerState.currentPage].imagePath else imageSrc.value[pagerState.currentPage].src.original,
                    context
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                SaveAllImagesButton(imageViewerViewModel, context, isFromGallery)
            }
        }
    }

    ProgressDialog(imageViewerViewModel)
    LoadingIndicator(imageViewerViewModel)
}

@Composable
fun DownloadButton(
    imageViewerViewModel: ImageViewerViewModel,
    imageSrc: String?,
    context: Context,
    modifier: Modifier = Modifier
) {
//    val isExists = imageViewerViewModel.isImageExistsInDb.collectAsState()

    Button(
        onClick = {
//            if (!isExists.value) {
                imageViewerViewModel.saveImage(imageSrc, context)
//            } else {
//                Toast.makeText(context, "Already Saved.", Toast.LENGTH_SHORT).show()
//            }
        },
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
//                    colors = if (isExists.value) {
//                        listOf(Color(0xFF42f54b), Color(0xFF016106))
//                    } else {
                        listOf(Color(0xFF00B4DB), Color(0xFF0083B0))
//                    }
                ),
                shape = MaterialTheme.shapes.large
            ),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = /*if (isExists.value) "Saved" else*/ "Save",
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun SaveAllImagesButton(
    imageViewerViewModel: ImageViewerViewModel,
    context: Context,
    isComingFromGallery: Boolean = false,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            imageViewerViewModel.saveAllImages(context, isComingFromGallery)
        },
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF00B4DB), Color(0xFF0083B0))
                ),
                shape = MaterialTheme.shapes.large
            ),
        colors = ButtonDefaults.buttonColors(Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = "Save All", color = Color.White, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun ProgressDialog(imageViewerViewModel: ImageViewerViewModel) {
    val progress = imageViewerViewModel.progress.collectAsState()
    val loading = imageViewerViewModel.loading.collectAsState()

    if (loading.value) {
        AlertDialog(
            onDismissRequest = { /* Optionally handle dismiss */ },
            title = {
                Text(text = "Saving Images")
            },
            text = {
                Column {
                    Text(text = "Saving ${progress.value.first}/${progress.value.second} images")
                    LinearProgressIndicator(
                        progress = progress.value.first / progress.value.second.toFloat(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {

            },
            dismissButton = {
                Button(onClick = {
                    imageViewerViewModel.cancelSaveOperation()
                }) {
                    Text("Cancel")
                }
            }
        )
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