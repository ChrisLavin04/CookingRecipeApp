package com.example.cookingrecipeapp.data

import kotlinx.coroutines.flow.Flow

class RecipeRepository(private val recipeDao: RecipeDao) {
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    fun getRecipe(id: Int): Flow<Recipe?> = recipeDao.getRecipe(id)
    fun getFavoriteRecipes(): Flow<List<Recipe>> = recipeDao.getFavoriteRecipes()
    fun getRecentlyViewedRecipes(): Flow<List<Recipe>> = recipeDao.getRecentlyViewedRecipes()
    fun searchRecipes(query: String): Flow<List<Recipe>> = recipeDao.searchRecipes(query)
    fun searchFavoriteRecipes(query: String): Flow<List<Recipe>> = recipeDao.searchFavoriteRecipes(query)
    fun searchRecentlyViewedRecipes(query: String): Flow<List<Recipe>> = recipeDao.searchRecentlyViewedRecipes(query)
    suspend fun insertRecipe(recipe: Recipe) = recipeDao.insertRecipe(recipe)
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
}
