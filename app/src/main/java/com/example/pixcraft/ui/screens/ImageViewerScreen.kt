package com.example.pixcraft.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
//    LaunchedEffect(pagerState) {
//        snapshotFlow { pagerState.settledPage }.collect { page ->
//            Log.d("Page change", "Page changed to $page")
//        }
//    }
    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize(1f)) { page ->
        val imageUrl = if (isFromGallery) {
            imageSrcGallery.value[page].imagePath
        } else {
            imageSrc.value[page].src.original
        }
        GlideImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            /*loading = placeholder {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LottiePlaceholder()
                }
            }*/
        )
//        if (page + 1 < imageSrc.value.size) {
//            Glide.with(context)
//                .load(imageSrc.value[page + 1].src.original)
//                .preload() // Preload the next image
//        }

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 40.dp)
                .align(Alignment.Start)
                .height(50.dp)
                .width(90.dp)
                .clip(MaterialTheme.shapes.large),
            contentAlignment = Alignment.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )


            Text(
                text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }


        DownloadButton(
            imageViewerViewModel, if (isFromGallery) {
                imageSrcGallery.value[pagerState.currentPage].imagePath
            } else {
                imageSrc.value[pagerState.currentPage].src.original
            }, context
        )
    }

    LoadingIndicator(imageViewerViewModel)
}

@Composable
fun DownloadButton(
    imageViewerViewModel: ImageViewerViewModel,
    imageSrc: String?,
    context: Context,
) {
    val isExists = imageViewerViewModel.isImageExistsInDb.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            onClick = {
                if (!isExists.value) {
                    imageViewerViewModel.saveImage(imageSrc, context)
                } else {
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