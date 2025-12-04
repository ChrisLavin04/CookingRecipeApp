package com.example.cookingrecipeapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cookingrecipeapp.data.Recipe
import com.example.cookingrecipeapp.data.AppDatabase
import com.example.cookingrecipeapp.data.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecipeRepository
    val recipes: StateFlow<List<Recipe>>
    val favoriteRecipes: StateFlow<List<Recipe>>
    val recentlyViewedRecipes: StateFlow<List<Recipe>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = RecipeRepository(db.recipeDao())
        recipes = repository.getAllRecipes().stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
        favoriteRecipes = repository.getFavoriteRecipes().stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
        recentlyViewedRecipes = repository.getRecentlyViewedRecipes().stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            emptyList()
        )
    }

    fun searchRecipes(query: String): Flow<List<Recipe>> {
        return if (query.isEmpty()) {
            repository.getAllRecipes()
        } else {
            repository.searchRecipes(query)
        }
    }

    fun searchFavoriteRecipes(query: String): Flow<List<Recipe>> {
        return if (query.isEmpty()) {
            repository.getFavoriteRecipes()
        } else {
            repository.searchFavoriteRecipes(query)
        }
    }

    fun searchRecentlyViewedRecipes(query: String): Flow<List<Recipe>> {
        return if (query.isEmpty()) {
            repository.getRecentlyViewedRecipes()
        } else {
            repository.searchRecentlyViewedRecipes(query)
        }
    }

    fun getRecipe(id: Int): Flow<Recipe?> = repository.getRecipe(id)

    fun insert(recipe: Recipe) = viewModelScope.launch {
        repository.insertRecipe(recipe)
    }

    fun update(recipe: Recipe) = viewModelScope.launch {
        repository.updateRecipe(recipe)
    }

    fun delete(recipe: Recipe) = viewModelScope.launch {
        repository.deleteRecipe(recipe)
    }

    fun toggleFavorite(recipe: Recipe) = viewModelScope.launch {
        repository.updateRecipe(recipe.copy(isFavorite = !recipe.isFavorite))
    }

    fun markAsViewed(recipe: Recipe) = viewModelScope.launch {
        repository.updateRecipe(recipe.copy(isViewed = true))
    }
}

class RecipeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
