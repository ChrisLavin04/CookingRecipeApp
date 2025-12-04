package com.example.cookingrecipeapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipe(id: Int): Flow<Recipe?>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isViewed = 1")
    fun getRecentlyViewedRecipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 AND (name LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%')")
    fun searchFavoriteRecipes(query: String): Flow<List<Recipe>>

    @Query("SELECT * FROM recipes WHERE isViewed = 1 AND (name LIKE '%' || :query || '%' OR ingredients LIKE '%' || :query || '%')")
    fun searchRecentlyViewedRecipes(query: String): Flow<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
}
