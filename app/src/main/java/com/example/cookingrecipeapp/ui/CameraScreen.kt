package com.example.cookingrecipeapp.ui

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * CameraScreen composable that provides camera functionality for capturing recipe images
 * Uses CameraX API for camera preview and image capture
 * 
 * @param navController Navigation controller for screen navigation
 * @param onImageCaptured Callback function invoked when image is successfully captured
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    onImageCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // State for camera permission
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }
    
    // Launcher for requesting camera permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }
    
    // Request permission on first composition
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Take Photo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (hasCameraPermission) {
            CameraPreviewScreen(
                modifier = Modifier.padding(paddingValues),
                context = context,
                lifecycleOwner = lifecycleOwner,
                onImageCaptured = { uri ->
                    onImageCaptured(uri)
                    navController.popBackStack()
                },
                onError = { exception ->
                    // Handle error - could show a snackbar or dialog
                    exception.printStackTrace()
                }
            )
        } else {
            // Show permission denied message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Camera permission is required to take photos")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                        Text("Grant Permission")
                    }
                }
            }
        }
    }
}

/**
 * Internal composable that handles camera preview and capture functionality
 */
@Composable
private fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    val executor = ContextCompat.getMainExecutor(context)
    
    Box(modifier = modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProvider = cameraProviderFuture.get()
                
                // Preview use case
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                
                // ImageCapture use case
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()
                
                // Select back camera as default
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    // Unbind all use cases before rebinding
                    cameraProvider.unbindAll()
                    
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    exc.printStackTrace()
                }
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Capture button at bottom center
        FloatingActionButton(
            onClick = {
                // Capture image when button clicked
                imageCapture?.let { capture ->
                    val photoFile = createImageFile(context)
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                    
                    capture.takePicture(
                        outputOptions,
                        executor,
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = Uri.fromFile(photoFile)
                                onImageCaptured(savedUri)
                            }
                            
                            override fun onError(exception: ImageCaptureException) {
                                onError(exception)
                            }
                        }
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Camera,
                contentDescription = "Capture photo",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Creates a file in the app's private storage for saving captured images
 * Files are named with timestamp to ensure uniqueness
 */
private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.filesDir
    return File.createTempFile(
        "RECIPE_${timeStamp}_",
        ".jpg",
        storageDir
    )
}
