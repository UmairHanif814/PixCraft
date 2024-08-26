package com.example.pixcraft.ui.screens

import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.example.pixcraft.utils.BlackWhiteTransformation
import com.example.pixcraft.utils.BrightnessTransformation
import com.example.pixcraft.utils.ContrastTransformation
import com.example.pixcraft.utils.CoolTransformation
import com.example.pixcraft.utils.GlideTransformation
import com.example.pixcraft.utils.GrayscaleTransformation
import com.example.pixcraft.utils.InvertColorsTransformation
import com.example.pixcraft.utils.NegativeTransformation
import com.example.pixcraft.utils.SaturationTransformation
import com.example.pixcraft.utils.SepiaTransformation
import com.example.pixcraft.utils.VintageTransformation
import com.example.pixcraft.utils.WarmTransformation
import com.example.pixcraft.viewmodels.SavedImageViewerViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SavedImagesViewerScreen() {
    val imageViewerViewModel: SavedImageViewerViewModel = hiltViewModel()
    val imageSrc = imageViewerViewModel.imageSrc.collectAsState()
    val context = LocalContext.current

    val selectedFilter = imageViewerViewModel.selectedFilter.collectAsState()
    val transformation = getTransformation(selectedFilter.value)

    // The image takes the full size of the screen
    GlideImage(
        model = imageSrc.value,
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        requestBuilderTransform = { builder ->
            transformation?.let {
                builder.transform(it)
            } ?: builder
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
    Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {


        FilterList(
            filters = imageViewerViewModel.filters,
            selectedFilter = selectedFilter.value,
            onFilterSelected = { filter ->
                imageViewerViewModel.updateSelectedFilter(filter)
            }, imageSrc.value
        )
        SetAsWallpaperButton(imageViewerViewModel, context, transformation)
    }
    LoadingIndicatorView(imageViewerViewModel)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FilterList(
    filters: List<GlideTransformation>,
    selectedFilter: GlideTransformation,
    onFilterSelected: (GlideTransformation) -> Unit,
    image: String?,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { filter ->
            val isSelected = filter == selectedFilter
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .width(120.dp)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.Green else Color.White,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onFilterSelected(filter) }
                    .clip(RoundedCornerShape(12.dp)), // Clip the Box to rounded corners
                contentAlignment = Alignment.BottomCenter
            ) {

                GlideImage(
                    model = image,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)), // Clip the image to match the Box's corners
                    contentScale = ContentScale.Crop,
                    requestBuilderTransform = { builder ->
                        getTransformation(filter)?.let { builder.transform(it) } ?: builder
                    },
                )
                Box(modifier = Modifier.fillMaxWidth().background(Color.Black)){
                    Text(
                        text = filter.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.BottomCenter).padding(5.dp)
                    )
                }


            }

        }
    }
}

@Composable
fun SetAsWallpaperButton(
    imageViewerViewModel: SavedImageViewerViewModel,
    context: Context,
    transformation: BitmapTransformation?
) {
    val wallpaperStatus by imageViewerViewModel.wallpaperStatus

    Box(

    ) {
        Button(
            onClick = {
                showWallpaperOptions(context, imageViewerViewModel, transformation)
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

        if (wallpaperStatus.isNotEmpty()) {
            Toast.makeText(context, wallpaperStatus, Toast.LENGTH_SHORT).show()
            imageViewerViewModel.setWallpaperStateEmpty()
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

private fun getTransformation(filter: GlideTransformation): BitmapTransformation? {
    return when (filter) {
        GlideTransformation.GrayScale -> GrayscaleTransformation()
        GlideTransformation.Sepia -> SepiaTransformation()
        GlideTransformation.InvertColors -> InvertColorsTransformation()
        GlideTransformation.BlackWhite -> BlackWhiteTransformation()
        GlideTransformation.Contrast -> ContrastTransformation(10f)
        GlideTransformation.Cool -> CoolTransformation()
        GlideTransformation.Brightness -> BrightnessTransformation(3f)
        GlideTransformation.Negative -> NegativeTransformation()
        GlideTransformation.Saturation -> SaturationTransformation(5f)
        GlideTransformation.Vintage -> VintageTransformation()
        GlideTransformation.Warmth -> WarmTransformation()
        else -> null
    }
}

private fun showWallpaperOptions(
    context: Context,
    viewModel: SavedImageViewerViewModel,
    transformation: BitmapTransformation?
) {
    val options = arrayOf("Home Screen", "Lock Screen", "Both")
    val wallpaperManager = WallpaperManager.getInstance(context)

    AlertDialog.Builder(context)
        .setTitle("Set as Wallpaper")
        .setItems(options) { dialog, which ->
            when (which) {
                0 -> viewModel.setAsWallpaper(context, WallpaperManager.FLAG_SYSTEM, transformation)
                1 -> viewModel.setAsWallpaper(context, WallpaperManager.FLAG_LOCK, transformation)
                2 -> viewModel.setAsWallpaper(
                    context,
                    WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK,
                    transformation
                )
            }
            dialog.dismiss()
        }
        .show()
}