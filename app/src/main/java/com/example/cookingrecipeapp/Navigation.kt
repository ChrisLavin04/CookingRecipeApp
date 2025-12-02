package com.example.cookingrecipeapp

import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("favourites") { FavouritesScreen(navController) }
        composable("history") { HistoryScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("create_recipe") { CreateRecipeScreen(navController) }
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
                EditRecipeScreen(navController = navController, recipeId = recipeId)
            }
        }
    }
}
