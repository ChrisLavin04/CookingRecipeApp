package com.example.cookingrecipeapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookingrecipeapp.data.Recipe
import com.example.cookingrecipeapp.data.RecipeDatabase
import com.example.cookingrecipeapp.data.RecipeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration tests for Recipe repository and database operations
 * Tests complete workflows from repository through database
 */
@RunWith(AndroidJUnit4::class)
class RecipeIntegrationTest {
    private lateinit var repository: RecipeRepository
    private lateinit var db: RecipeDatabase

    private val testRecipe = Recipe(
        id = 1,
        name = "Test Recipe",
        ingredients = "ingredient1, ingredient2",
        guide = "Test guide",
        image = 0,
        isFavorite = false,
        isViewed = false
    )

    @Before
    fun setupRepository() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RecipeDatabase::class.java)
            .allowMainThreadQueries().build()
        repository = RecipeRepository(db.recipeDao())
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun repositoryCompleteLifecycle_worksCorrectly() = runBlocking {
        // Insert
        repository.insertRecipe(testRecipe)
        var allRecipes = repository.getAllRecipes().first()
        assertEquals(1, allRecipes.size)
        
        // Mark as viewed
        val viewedRecipe = testRecipe.copy(isViewed = true)
        repository.updateRecipe(viewedRecipe)
        val viewedRecipes = repository.getRecentlyViewedRecipes().first()
        assertEquals(1, viewedRecipes.size)
        
        // Mark as favorite
        val favoriteRecipe = viewedRecipe.copy(isFavorite = true)
        repository.updateRecipe(favoriteRecipe)
        val favoriteRecipes = repository.getFavoriteRecipes().first()
        assertEquals(1, favoriteRecipes.size)
        
        // Update details
        val updatedRecipe = favoriteRecipe.copy(name = "Updated Name")
        repository.updateRecipe(updatedRecipe)
        val retrieved = repository.getRecipe(1).first()
        assertEquals("Updated Name", retrieved?.name)
        assertTrue(retrieved?.isFavorite ?: false)
        
        // Delete
        repository.deleteRecipe(updatedRecipe)
        allRecipes = repository.getAllRecipes().first()
        assertEquals(0, allRecipes.size)
    }

    @Test
    fun repositorySearchAndFilter_workCorrectly() = runBlocking {
        val pasta1 = testRecipe.copy(id = 1, name = "Spaghetti", ingredients = "pasta, eggs")
        val pasta2 = testRecipe.copy(id = 2, name = "Pasta Bolognese", ingredients = "pasta, beef", isFavorite = true)
        val curry = testRecipe.copy(id = 3, name = "Chicken Curry", ingredients = "chicken, curry", isViewed = true)
        
        repository.insertRecipe(pasta1)
        repository.insertRecipe(pasta2)
        repository.insertRecipe(curry)
        
        // Search
        val pastaResults = repository.searchRecipes("pasta").first()
        assertEquals(2, pastaResults.size)
        
        // Filter favorites
        val favorites = repository.getFavoriteRecipes().first()
        assertEquals(1, favorites.size)
        
        // Filter viewed
        val viewed = repository.getRecentlyViewedRecipes().first()
        assertEquals(1, viewed.size)
        
        // Counts
        assertEquals(3, repository.getRecipeCount().first())
        assertEquals(1, repository.getFavoriteCount().first())
        assertEquals(1, repository.getViewedCount().first())
    }
}
