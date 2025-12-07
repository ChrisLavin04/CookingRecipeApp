package com.example.cookingrecipeapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cookingrecipeapp.data.Recipe
import com.example.cookingrecipeapp.viewmodel.RecipeViewModel
import com.example.cookingrecipeapp.viewmodel.RecipeViewModelFactory
import android.app.Application
import android.net.Uri
import coil.compose.AsyncImage


@Composable
fun EditRecipeScreen(
    navController: NavController, 
    recipeId: Int,
    capturedImageUri: Uri? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val recipeViewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModelFactory(context.applicationContext as Application)
    )
    val recipeFlow = recipeViewModel.getRecipe(recipeId)
    val recipe by recipeFlow.collectAsStateWithLifecycle(initialValue = null)
    
    var recipeName by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var guide by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf(capturedImageUri) }
    var isLoaded by remember { mutableStateOf(false) }
    
    var showNameError by remember { mutableStateOf(false) }
    var showIngredientsError by remember { mutableStateOf(false) }
    var showGuideError by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Update image when new photo is captured
    LaunchedEffect(capturedImageUri) {
        if (capturedImageUri != null) {
            imageUri = capturedImageUri
        }
    }

    // Load recipe data once when it becomes available
    LaunchedEffect(recipe) {
        if (recipe != null && !isLoaded) {
            recipeName = recipe!!.name
            ingredients = recipe!!.ingredients
            guide = recipe!!.guide
            // Only load recipe image if no captured image exists
            if (imageUri == null) {
                imageUri = recipe!!.imagePath?.let { Uri.parse(it) }
            }
            isLoaded = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            BottomAppBar {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Home, contentDescription = "Home")
                    }
                    IconButton(onClick = { navController.navigate("history") }) {
                        Icon(Icons.Outlined.History, contentDescription = "History")
                    }
                    IconButton(onClick = { navController.navigate("create_recipe") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                    IconButton(onClick = { navController.navigate("favourites") }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favorites")
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Edit Recipe",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Camera image preview section
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Recipe image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Camera buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { navController.navigate("camera/edit/$recipeId") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(if (imageUri != null) "Retake" else "Take Photo")
                }
                
                if (imageUri != null) {
                    OutlinedButton(
                        onClick = { imageUri = null },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Clear")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = recipeName,
                onValueChange = { 
                    recipeName = it
                    showNameError = false
                },
                label = { Text("Recipe Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = showNameError,
                supportingText = if (showNameError) {{ Text("Recipe name is required") }} else null
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = ingredients,
                onValueChange = { 
                    ingredients = it
                    showIngredientsError = false
                },
                label = { Text("Ingredients") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                isError = showIngredientsError,
                supportingText = if (showIngredientsError) {{ Text("Ingredients are required") }} else null
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = guide,
                onValueChange = { 
                    guide = it
                    showGuideError = false
                },
                label = { Text("Guide") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6,
                isError = showGuideError,
                supportingText = if (showGuideError) {{ Text("Guide is required") }} else null
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                // Reset error states
                showNameError = recipeName.isBlank()
                showIngredientsError = ingredients.isBlank()
                showGuideError = guide.isBlank()
                
                if (recipeName.isNotBlank() && ingredients.isNotBlank() && guide.isNotBlank() && recipe != null) {
                    val updatedRecipe = recipe!!.copy(
                        name = recipeName,
                        ingredients = ingredients,
                        guide = guide,
                        imagePath = imageUri?.toString() // Update with new camera image
                    )
                    recipeViewModel.update(updatedRecipe)
                    navController.popBackStack()
                } else {
                    scope.launch {
                        snackbarHostState.showSnackbar("Please fill in all required fields")
                    }
                }
            }) {
                Text("Update Recipe")
            }
        }
    }
}
