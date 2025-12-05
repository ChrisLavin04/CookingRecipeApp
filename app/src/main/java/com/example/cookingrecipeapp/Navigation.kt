package com.example.cookingrecipeapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cookingrecipeapp.ui.CreateRecipeScreen
import com.example.cookingrecipeapp.ui.EditRecipeScreen
import com.example.cookingrecipeapp.ui.FavouritesScreen
import com.example.cookingrecipeapp.ui.HistoryScreen
import com.example.cookingrecipeapp.ui.ProfileScreen
import com.example.cookingrecipeapp.ui.RecipeDetailScreen
import com.example.cookingrecipeapp.ui.CameraScreen
import android.net.Uri

/**
 * Main navigation component for the Cooking Recipe App
 * Handles all screen navigation and state management between screens
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    // Shared state for captured image between camera and create screens
    var capturedImageForCreate by rememberSaveable { mutableStateOf<String?>(null) }
    var capturedImageForEdit by rememberSaveable { mutableStateOf<String?>(null) }
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("favourites") { FavouritesScreen(navController) }
        composable("history") { HistoryScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        
        // Create recipe with camera support
        composable("create_recipe") { 
            CreateRecipeScreen(
                navController = navController,
                capturedImageUri = capturedImageForCreate?.let { Uri.parse(it) }
            ) 
        }
        
        // Camera for create recipe
        composable("camera/create") {
            CameraScreen(navController = navController) { uri ->
                capturedImageForCreate = uri.toString()
                navController.popBackStack()
            }
        }
        
        // Camera for edit recipe
        composable(
            route = "camera/edit/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) {
            CameraScreen(navController = navController) { uri ->
                capturedImageForEdit = uri.toString()
                navController.popBackStack()
            }
        }
        
        composable(
            route = "recipe_detail/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId")
            if (recipeId != null) {
                RecipeDetailScreen(navController = navController, recipeId = recipeId)
            }
        }
        
        composable(
            route = "edit_recipe/{recipeId}",
            arguments = listOf(navArgument("recipeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getInt("recipeId")
            if (recipeId != null) {
                EditRecipeScreen(
                    navController = navController, 
                    recipeId = recipeId,
                    capturedImageUri = capturedImageForEdit?.let { Uri.parse(it) }
                )
            }
        }
    }
}
