package com.example.cookingrecipeapp

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.cookingrecipeapp.data.Recipe
import com.example.cookingrecipeapp.data.RecipeDatabase
import com.example.cookingrecipeapp.data.RecipeDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation tests for RecipeDatabase
 * Tests database persistence and key operations
 */
@RunWith(AndroidJUnit4::class)
class RecipeDatabaseTest {
    private lateinit var recipeDao: RecipeDao
    private lateinit var db: RecipeDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RecipeDatabase::class.java).build()
        recipeDao = db.recipeDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun databasePersistsAllRecipeFields() = runBlocking {
        val recipe = Recipe(
            id = 1,
            name = "Test Recipe",
            ingredients = "pasta, eggs",
            guide = "Cook and mix",
            image = 0,
            imagePath = "/storage/test.jpg",
            isFavorite = true,
            isViewed = true
        )
        recipeDao.insertRecipe(recipe)
        
        val retrieved = recipeDao.getRecipe(1).first()
        assertNotNull(retrieved)
        assertEquals(recipe.name, retrieved?.name)
        assertEquals(recipe.ingredients, retrieved?.ingredients)
        assertEquals(recipe.imagePath, retrieved?.imagePath)
        assertTrue(retrieved?.isFavorite ?: false)
        assertTrue(retrieved?.isViewed ?: false)
    }

    @Test
    fun databaseReplaceOnConflict() = runBlocking {
        val recipe1 = Recipe(id = 1, name = "Original", ingredients = "i", guide = "g", image = 0)
        recipeDao.insertRecipe(recipe1)
        
        val recipe2 = Recipe(id = 1, name = "Replaced", ingredients = "i2", guide = "g2", image = 0)
        recipeDao.insertRecipe(recipe2)
        
        val allRecipes = recipeDao.getAllRecipes().first()
        assertEquals(1, allRecipes.size)
        assertEquals("Replaced", allRecipes[0].name)
    }
}
