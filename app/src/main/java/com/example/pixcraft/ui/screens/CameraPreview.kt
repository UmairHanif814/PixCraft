package com.example.pixcraft.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.pixcraft.viewmodels.CameraViewModel
import com.example.pixcraft.viewmodels.GalleryImagesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreview() {
    val imagesViewModel: CameraViewModel = hiltViewModel()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State for camera permission
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(cameraPermissionState.status) {
        if (!cameraPermissionState.status.isGranted) {
//            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (cameraPermissionState.status.isGranted) {
        val cameraController = remember {
            LifecycleCameraController(context).apply {
                bindToLifecycle(lifecycleOwner)
            }
        }

        var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_START
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        controller = cameraController
                    }
                },
                onRelease = {
                    cameraController.unbind()
                }
            )

            FloatingActionButton(
                onClick = {
                    val photoFile =
                        File(context.cacheDir, "captured_image_${System.currentTimeMillis()}.jpg")
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    cameraController.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                capturedImageUri = Uri.fromFile(photoFile)
                                Toast.makeText(context, "Image captured!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onError(exception: ImageCaptureException) {
                                Toast.makeText(
                                    context,
                                    "Capture failed: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = "Capture")
            }

            capturedImageUri?.let { uri ->
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(data = uri),
                        contentDescription = "Captured Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Button(
                        onClick = {
                            imagesViewModel.saveImageToLocalStorage(context, uri)
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Camera permission is required to use the camera.")

                // Button to open settings if permission was permanently denied
                if (cameraPermissionState.status.shouldShowRationale) {
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }) {
                        Text(text = "Open Settings")
                    }
                } else {
                    Button(onClick = {
                        cameraPermissionState.launchPermissionRequest()
                    }) {
                        Text(text = "Request Permission")
                    }
                }
            }
        }
    }
}



