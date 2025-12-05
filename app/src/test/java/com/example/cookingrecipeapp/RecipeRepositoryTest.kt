package com.example.cookingrecipeapp

import com.example.cookingrecipeapp.data.Recipe
import com.example.cookingrecipeapp.data.RecipeDao
import com.example.cookingrecipeapp.data.RecipeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify

/**
 * Unit tests for RecipeRepository
 * Tests repository layer delegates to DAO correctly
 */
class RecipeRepositoryTest {

    private lateinit var repository: RecipeRepository
    private lateinit var mockDao: RecipeDao

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
    fun setup() {
        mockDao = mock(RecipeDao::class.java)
        repository = RecipeRepository(mockDao)
    }

    @Test
    fun repositoryDelegatesQueriesToDao() = runTest {
        `when`(mockDao.getAllRecipes()).thenReturn(flowOf(listOf(testRecipe)))
        `when`(mockDao.getRecipe(1)).thenReturn(flowOf(testRecipe))
        `when`(mockDao.getFavoriteRecipes()).thenReturn(flowOf(listOf(testRecipe)))
        `when`(mockDao.getRecipeCount()).thenReturn(flowOf(1))
        
        assertEquals(1, repository.getAllRecipes().first().size)
        assertEquals(testRecipe.name, repository.getRecipe(1).first()?.name)
        assertEquals(1, repository.getFavoriteRecipes().first().size)
        assertEquals(1, repository.getRecipeCount().first())
    }

    @Test
    fun repositoryDelegatesCrudOperationsToDao() = runTest {
        repository.insertRecipe(testRecipe)
        verify(mockDao).insertRecipe(testRecipe)
        
        repository.updateRecipe(testRecipe)
        verify(mockDao).updateRecipe(testRecipe)
        
        repository.deleteRecipe(testRecipe)
        verify(mockDao).deleteRecipe(testRecipe)
    }
}
