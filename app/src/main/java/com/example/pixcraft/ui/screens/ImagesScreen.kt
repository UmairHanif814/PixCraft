package com.example.pixcraft.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pixcraft.R
import com.example.pixcraft.models.Photo
import com.example.pixcraft.models.PixCraftModel
import com.example.pixcraft.models.Src
import com.example.pixcraft.viewmodels.ImagesViewModel

@Composable
fun ImagesScreen(onItemClick: (Src) -> Unit, onSavedImagesClick: () -> Unit) {
    val imagesViewModel: ImagesViewModel = hiltViewModel()
    val images: State<PixCraftModel?> = imagesViewModel.images.collectAsState()
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val isNetworkAvailable = imagesViewModel.isNetworkAvailable.value
    val context = LocalContext.current
    if (isNetworkAvailable) {
        Column {
            Spacer(modifier = Modifier.height(40.dp))
            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth(),
                enabled = true,
                readOnly = false,
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 0.5.sp,
                    textDecoration = TextDecoration.None,
                    textAlign = TextAlign.Start
                ),
                placeholder = { Text("Search") },
                // at the start of text field.
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        tint = Color.Black
                    )
                },
                isError = false,
                visualTransformation = VisualTransformation.None,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (text.isNotBlank()) {
                            imagesViewModel.getSearchedImages(text)
                            Log.d("ImagesScreen", "ImagesScreen: ${imagesViewModel.images.value}")
                            text = ""
                            keyboardController?.hide()
                        }
                    }
                ),
                singleLine = true,
                maxLines = 1,
                minLines = 1,
                interactionSource = remember { MutableInteractionSource() },
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Green,
                    disabledTextColor = Color.Green,
                    focusedContainerColor = Color.LightGray,
                    unfocusedContainerColor = Color.LightGray,
                    cursorColor = Color.Gray,
                    errorCursorColor = Color.Red,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            images.value?.let {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    items(it.photos) {
                        ImageCard(image = it) { photo ->
                            onItemClick(photo)
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
    SavedImagesButton {
        onSavedImagesClick()
    }

}

@Composable
fun SavedImagesButton(
    onSavedImagesClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            onClick = {
                onSavedImagesClick()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF42f54b), Color(0xFF016106))
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                Color.Transparent
            ),
            contentPadding = PaddingValues()
        ) {

            Text(
                text = "Saved Images", color = Color.White, modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageCard(image: Photo, onClick: (Src) -> Unit) {
    Box(
        modifier = Modifier
            .padding(4.dp)
            .size(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(width = 1.dp, Color(0xFFEEEEEE))
            .clickable { onClick(image.src) },
        contentAlignment = Alignment.BottomCenter
    ) {
        GlideImage(
            model = image.src.tiny,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(1f),
            contentScale = ContentScale.Crop,
//            loading = placeholder {
//                LottiePlaceholder()
//            }
        )
    }
}

@Composable
fun LottiePlaceholder(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading2))
    LottieAnimation(
        composition = composition,
        modifier = modifier,
        iterations = LottieConstants.IterateForever
    )
}