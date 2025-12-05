package com.example.cookingrecipeapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookingrecipeapp.data.Recipe
import com.example.cookingrecipeapp.data.RecipeDao
import com.example.cookingrecipeapp.data.RecipeDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit tests for RecipeDao database operations
 * Tests all CRUD operations and query functions
 */
@RunWith(AndroidJUnit4::class)
class RecipeDaoTest {
    private lateinit var recipeDao: RecipeDao
    private lateinit var recipeDatabase: RecipeDatabase

    private val testRecipe1 = Recipe(
        id = 1,
        name = "Spaghetti Carbonara",
        ingredients = "pasta, eggs, bacon, parmesan",
        guide = "Cook pasta. Mix eggs and cheese. Combine with bacon.",
        image = 0,
        imagePath = null,
        isFavorite = false,
        isViewed = false
    )

    private val testRecipe2 = Recipe(
        id = 2,
        name = "Chicken Curry",
        ingredients = "chicken, curry powder, coconut milk, onion",
        guide = "Sauté onions. Add chicken and curry. Simmer with coconut milk.",
        image = 0,
        imagePath = null,
        isFavorite = true,
        isViewed = true
    )

    private val testRecipe3 = Recipe(
        id = 3,
        name = "Caesar Salad",
        ingredients = "lettuce, croutons, parmesan, caesar dressing",
        guide = "Toss lettuce with dressing. Add croutons and cheese.",
        image = 0,
        imagePath = null,
        isFavorite = false,
        isViewed = true
    )

    @Before
    fun createDb() = runBlocking {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        recipeDatabase = Room.inMemoryDatabaseBuilder(context, RecipeDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        recipeDao = recipeDatabase.recipeDao()
    }

    @After
    fun closeDb() {
        recipeDatabase.close()
    }

    @Test
    fun daoCrudOperations_workCorrectly() = runBlocking {
        // Insert
        recipeDao.insertRecipe(testRecipe1)
        var allRecipes = recipeDao.getAllRecipes().first()
        assertEquals(1, allRecipes.size)
        
        // Get by ID
        var recipe = recipeDao.getRecipe(1).first()
        assertNotNull(recipe)
        assertEquals(testRecipe1.name, recipe?.name)
        
        // Update
        val updatedRecipe = testRecipe1.copy(name = "Updated Carbonara", isFavorite = true)
        recipeDao.updateRecipe(updatedRecipe)
        recipe = recipeDao.getRecipe(1).first()
        assertEquals("Updated Carbonara", recipe?.name)
        assertTrue(recipe?.isFavorite ?: false)
        
        // Delete
        recipeDao.deleteRecipe(updatedRecipe)
        recipe = recipeDao.getRecipe(1).first()
        assertNull(recipe)
    }

    @Test
    fun daoFilterAndSearch_workCorrectly() = runBlocking {
        addThreeRecipesToDb()
        
        // Test favorites filter
        val favorites = recipeDao.getFavoriteRecipes().first()
        assertEquals(1, favorites.size)
        assertTrue(favorites[0].isFavorite)
        
        // Test viewed filter
        val viewed = recipeDao.getRecentlyViewedRecipes().first()
        assertEquals(2, viewed.size)
        assertTrue(viewed.all { it.isViewed })
        
        // Test search by name (case-insensitive)
        val searchResults = recipeDao.searchRecipes("chicken").first()
        assertEquals(1, searchResults.size)
        
        // Test search by ingredients
        val ingredientSearch = recipeDao.searchRecipes("parmesan").first()
        assertEquals(2, ingredientSearch.size)
        
        // Test search favorites
        val favoriteSearch = recipeDao.searchFavoriteRecipes("Chicken").first()
        assertEquals(1, favoriteSearch.size)
        assertTrue(favoriteSearch[0].isFavorite)
    }

    @Test
    fun daoCountOperations_returnCorrectCounts() = runBlocking {
        addThreeRecipesToDb()
        
        assertEquals(3, recipeDao.getRecipeCount().first())
        assertEquals(1, recipeDao.getFavoriteCount().first())
        assertEquals(2, recipeDao.getViewedCount().first())
    }

    private suspend fun addOneRecipeToDb() {
        recipeDao.insertRecipe(testRecipe1)
    }

    private suspend fun addTwoRecipesToDb() {
        recipeDao.insertRecipe(testRecipe1)
        recipeDao.insertRecipe(testRecipe2)
    }

    private suspend fun addThreeRecipesToDb() {
        recipeDao.insertRecipe(testRecipe1)
        recipeDao.insertRecipe(testRecipe2)
        recipeDao.insertRecipe(testRecipe3)
    }
}
