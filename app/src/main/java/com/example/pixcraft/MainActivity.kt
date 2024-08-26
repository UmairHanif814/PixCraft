package com.example.pixcraft

import android.os.Bundle
import android.provider.Settings.Global
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pixcraft.api.PixCraftApi
import com.example.pixcraft.models.Src
import com.example.pixcraft.ui.screens.ImageViewerScreen
import com.example.pixcraft.ui.screens.ImagesScreen
import com.example.pixcraft.ui.screens.SavedImagesScreen
import com.example.pixcraft.ui.screens.SavedImagesViewerScreen
import com.example.pixcraft.ui.theme.PixCraftTheme
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URLDecoder
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
}

@Composable
fun App() {
    val navController = rememberNavController()
    val gson = Gson()
    NavHost(navController = navController, startDestination = "ImagesScreen") {
        composable(route = "ImagesScreen") {
            ImagesScreen(onItemClick = {
                val srcJson = gson.toJson(it)
                val encodedSrcJson = URLEncoder.encode(srcJson, "UTF-8")
                navController.navigate("ImageViewerScreen/${encodedSrcJson}")
            }, onSavedImagesClick = {
                navController.navigate("SavedImagesScreen")
            })

        }
        composable(route = "ImageViewerScreen/{imageSrc}", arguments = listOf(
            navArgument("imageSrc") {
                type = NavType.StringType
            }
        )) {
            /*val encodedSrcJson = it.arguments?.getString("imageSrc") ?: ""
            val srcJson = URLDecoder.decode(encodedSrcJson, "UTF-8")
            val src = gson.fromJson(srcJson, Src::class.java)*/
            ImageViewerScreen()
        }
        composable(route="SavedImagesScreen"){
            SavedImagesScreen{
                val srcJson = gson.toJson(it)
                val encodedSrcJson = URLEncoder.encode(srcJson, "UTF-8")
                navController.navigate("SavedImageViewerScreen/${encodedSrcJson}")
            }
        }
        composable(route="SavedImageViewerScreen/{image}", arguments = listOf(
            navArgument("image") {
                type = NavType.StringType
            }
        )){
            SavedImagesViewerScreen()
        }
    }
}
