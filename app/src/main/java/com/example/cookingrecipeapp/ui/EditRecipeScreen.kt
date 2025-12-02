package com.example.cookingrecipeapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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


@Composable
fun EditRecipeScreen(navController: NavController, recipeId: Int) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val recipeViewModel: RecipeViewModel = viewModel(
        factory = RecipeViewModelFactory(context.applicationContext as Application)
    )
    val recipeFlow = recipeViewModel.getRecipe(recipeId)
    val recipe by recipeFlow.collectAsStateWithLifecycle(initialValue = null)
    
    var recipeName by remember { mutableStateOf("") }
    var ingredients by remember { mutableStateOf("") }
    var guide by remember { mutableStateOf("") }
    var isLoaded by remember { mutableStateOf(false) }

    // Load recipe data once when it becomes available
    LaunchedEffect(recipe) {
        if (recipe != null && !isLoaded) {
            recipeName = recipe!!.name
            ingredients = recipe!!.ingredients
            guide = recipe!!.guide
            isLoaded = true
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
                text = "Edit Recipe",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
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
                if (recipeName.isNotBlank() && ingredients.isNotBlank() && guide.isNotBlank() && recipe != null) {
                    val updatedRecipe = recipe!!.copy(
                        name = recipeName,
                        ingredients = ingredients,
                        guide = guide
                    )
                    recipeViewModel.update(updatedRecipe)
                    navController.popBackStack()
                }
            }) {
                Text("Update Recipe")
            }
        }
    }
}
