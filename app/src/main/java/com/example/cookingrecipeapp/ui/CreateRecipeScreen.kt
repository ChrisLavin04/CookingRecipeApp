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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookingrecipeapp.data.Recipe
import com.example.cookingrecipeapp.ui.theme.CookingRecipeAppTheme
import com.example.cookingrecipeapp.viewmodel.RecipeViewModel
import com.example.cookingrecipeapp.viewmodel.RecipeViewModelFactory
import android.app.Application
import android.net.Uri
import coil.compose.AsyncImage

@Composable
fun CreateRecipeScreen(
    navController: NavController,
    capturedImageUri: Uri? = null
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val recipeViewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModelFactory(context.applicationContext as Application)
    )
    var recipeName by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var guide by remember { mutableStateOf("") }
    // Use provided captured image or maintain local state
    var imageUri by remember { mutableStateOf(capturedImageUri) }
    
    // Update image when capturedImageUri changes
    androidx.compose.runtime.LaunchedEffect(capturedImageUri) {
        if (capturedImageUri != null) {
            imageUri = capturedImageUri
        }
    }

    Scaffold(
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Recipe",
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
            
            // Camera button
            OutlinedButton(
                onClick = { navController.navigate("camera/create") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.padding(4.dp))
                Text(if (imageUri != null) "Retake Photo" else "Take Photo")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = recipeName,
                onValueChange = { recipeName = it },
                label = { Text("Recipe Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = ingredients,
                onValueChange = { ingredients = it },
                label = { Text("Ingredients") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = guide,
                onValueChange = { guide = it },
                label = { Text("Guide") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 6
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (recipeName.isNotBlank() && ingredients.isNotBlank() && guide.isNotBlank()) {
                    val recipe = Recipe(
                        name = recipeName,
                        ingredients = ingredients,
                        guide = guide,
                        image = 0, // No drawable resource
                        imagePath = imageUri?.toString() // Save camera image path
                    )
                    recipeViewModel.insert(recipe)
                    navController.navigate("home")
                }
            }) {
                Text("Create Recipe")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateRecipeScreenPreview() {
    CookingRecipeAppTheme {
        CreateRecipeScreen(rememberNavController())
    }
}
