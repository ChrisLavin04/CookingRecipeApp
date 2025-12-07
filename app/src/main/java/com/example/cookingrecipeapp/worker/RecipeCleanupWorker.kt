package com.example.cookingrecipeapp.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cookingrecipeapp.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * WorkManager worker that performs periodic cleanup of unused recipe images
 * 
 * USE CASE:
 * When users create recipes with photos and later delete those recipes, or when they
 * retake/clear photos, the old image files remain in storage indefinitely. Over time,
 * this accumulates "orphaned" images that waste valuable device storage.
 * 
 * This background task automatically:
 * - Scans all recipe image files in the app's private storage
 * - Queries the database to get all currently active recipe image paths
 * - Identifies and deletes unused files
 * 
 * SCHEDULING:
 * - Runs periodically every 24 hours (configured in RecipeApplication)
 * - Only executes when device is charging to preserve battery
 * - Can be manually triggered from Profile screen for testing/demonstration
 * 
 * BENEFITS:
 * - Prevents storage bloat from accumulating unused images
 * - Improves app performance by maintaining clean storage
 * - Runs in background without impacting user experience
 * - Battery-efficient execution (only when charging)
 */
class RecipeCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "RecipeCleanupWorker"
        const val WORK_NAME = "recipe_cleanup_work"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Starting recipe cleanup task")
            
            // Get all recipes from database
            val database = AppDatabase.getDatabase(applicationContext)
            val recipes = database.recipeDao().getAllRecipesOnce()
            
            // Get all image paths currently in use - extract just the filenames for comparison
            val usedImageFilenames = recipes
                .mapNotNull { it.imagePath }
                .filter { it.isNotEmpty() }
                .mapNotNull { path ->
                    // Extract filename from URI path (e.g., "file:///.../.../RECIPE_123.jpg" -> "RECIPE_123.jpg")
                    path.substringAfterLast('/')
                }
                .toSet()
            
            Log.d(TAG, "Found ${usedImageFilenames.size} images in use by recipes")
            
            // Get all image files in storage
            val storageDir = applicationContext.filesDir
            val imageFiles = storageDir.listFiles { file ->
                file.name.startsWith("RECIPE_") && file.name.endsWith(".jpg")
            } ?: emptyArray()
            
            Log.d(TAG, "Found ${imageFiles.size} image files in storage")
            
            // Delete unused image files - compare by filename
            var deletedCount = 0
            imageFiles.forEach { file ->
                if (!usedImageFilenames.contains(file.name)) {
                    if (file.delete()) {
                        deletedCount++
                        Log.d(TAG, "Deleted unused image: ${file.name}")
                    } else {
                        Log.w(TAG, "Failed to delete unused image: ${file.name}")
                    }
                } else {
                    Log.d(TAG, "Keeping image in use: ${file.name}")
                }
            }
            
            Log.d(TAG, "Cleanup complete. Deleted $deletedCount unused images out of ${imageFiles.size} total")
            
            // Return success
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error during cleanup task", e)
            // Retry if we encounter an error
            Result.retry()
        }
    }
}
