package com.example.pixcraft.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pixcraft.models.MediaStoreImagesModel
import com.example.pixcraft.viewmodels.GalleryImagesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GalleryImagesScreen(onItemClick: (List<MediaStoreImagesModel>, Int) -> Unit) {
    val imagesViewModel: GalleryImagesViewModel = hiltViewModel()
    val images: State<List<MediaStoreImagesModel>> = imagesViewModel.images.collectAsState()
    val isNetworkAvailable = imagesViewModel.isNetworkAvailable.value
    val context = LocalContext.current
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    // Check for permissions
    val cameraPermissionState = rememberPermissionState(permission = permissions)

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted) {
            imagesViewModel.getGalleryImages(context)
        }
    }

    if (cameraPermissionState.status.isGranted) {
        if (isNetworkAvailable) {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                images.value?.let { list ->
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        itemsIndexed(list) { index, item ->
                            ImageCardGallery(image = item) { photo ->
                                onItemClick(list, index)
                            }
                        }
                    }
                } ?: run {
                    Box(modifier = Modifier.fillMaxSize(1f), contentAlignment = Alignment.Center) {
                        LottiePlaceholder()
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Network Unavailable")
                    Button(onClick = { imagesViewModel.checkNetworkState(context) }) {
                        Text(text = "Retry")
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Permission to access gallery is required.")

            Spacer(modifier = Modifier.height(16.dp))

            if (!cameraPermissionState.status.shouldShowRationale) {
                Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                    Text("Grant Permission")
                }
            } else {
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageCardGallery(image: MediaStoreImagesModel, onClick: (MediaStoreImagesModel) -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, Color(0xFFEEEEEE))
            .clickable { onClick(image) },
        contentAlignment = Alignment.BottomCenter
    ) {
        GlideImage(
            model = image.imagePath,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            contentScale = ContentScale.Crop,
//            loading = placeholder {
//                LottiePlaceholder()
//            }
        )
    }
}