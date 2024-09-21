package com.example.pixcraft

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pixcraft.api.PixCraftApi
import com.example.pixcraft.ui.screens.CameraPreview
import com.example.pixcraft.ui.screens.GalleryImagesScreen
import com.example.pixcraft.ui.screens.ImageViewerScreen
import com.example.pixcraft.ui.screens.ImagesScreen
import com.example.pixcraft.ui.screens.SavedImagesScreen
import com.example.pixcraft.ui.screens.SavedImagesViewerScreen
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var pixCraftApi: PixCraftApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
    private val _isCameraPermissionGranted = MutableStateFlow(false)
    val isCameraPermissionGranted: StateFlow<Boolean> = _isCameraPermissionGranted
    private val cameraPermissionRequestLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, update the state
                _isCameraPermissionGranted.value = true
            } else {
                // Permission denied: inform the user to enable it through settings
                Toast.makeText(
                    this,
                    "Go to settings and enable camera permission to use this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    fun handleCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted, update the state
                _isCameraPermissionGranted.value = true
            }

            else -> {
                cameraPermissionRequestLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

}


@Composable
fun App() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "MainScreen") {
        composable("MainScreen") {
            MainScreen(navController = navController)
        }
        composable(
            route = "ImageViewerScreen/{imageSrc}/{initialPage}?isFromGallery={isFromGallery}",
            arguments = listOf(
                navArgument("imageSrc") { type = NavType.StringType },
                navArgument("initialPage") { type = NavType.IntType },
                navArgument("isFromGallery") { type = NavType.BoolType }
            )
        ) {
            val isFromGallery = it.arguments?.getBoolean("isFromGallery") ?: false
            ImageViewerScreen(isFromGallery)
        }
        // Navigation to SavedImageViewerScreen
        composable(
            route = "SavedImageViewerScreen/{image}",
            arguments = listOf(
                navArgument("image") { type = NavType.StringType }
            )
        ) {
            SavedImagesViewerScreen()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(navController: NavController) {
    val gson = remember { Gson() }
    val tabs = listOf("API", "Gallery","Camera", "Saved Images")
    val pagerState = rememberPagerState {
        tabs.size
    }
    val scope = rememberCoroutineScope()

    Column {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(text = title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> ImagesScreen(
                    onItemClick = { list, index ->
                        val srcJson = gson.toJson(list)
                        val encodedSrcJson = URLEncoder.encode(srcJson, "UTF-8")
                        navController.navigate("ImageViewerScreen/${encodedSrcJson}/$index?isFromGallery=false")
                    },
                )

                1 -> {
                    GalleryImagesScreen { list, index ->
                        val srcJson = gson.toJson(list)
                        val encodedSrcJson = URLEncoder.encode(srcJson, "UTF-8")
                        navController.navigate("ImageViewerScreen/${encodedSrcJson}/$index?isFromGallery=true")
                    }
                }
                2->{
                    CameraPreview()
                }
                3 -> SavedImagesScreen { image ->
                    val srcJson = gson.toJson(image)
                    val encodedSrcJson = URLEncoder.encode(srcJson, "UTF-8")
                    navController.navigate("SavedImageViewerScreen/${encodedSrcJson}")
                }
            }
        }
    }
}

