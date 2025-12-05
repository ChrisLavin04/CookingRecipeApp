package com.example.cookingrecipeapp

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cookingrecipeapp.worker.RecipeCleanupWorker
import java.util.concurrent.TimeUnit

/**
 * Application class for the Cooking Recipe App
 * Handles app-wide initialization including WorkManager background tasks
 */
class RecipeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Schedule periodic cleanup work
        scheduleCleanupWork()
    }

    /**
     * Schedules a periodic WorkManager task to clean up unused recipe images
     * Runs every 24 hours when device is idle and charging (to save battery)
     */
    private fun scheduleCleanupWork() {
        // Define constraints for when the work should run
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)  // Run when device is charging
            .build()

        // Create a periodic work request that runs every 24 hours
        val cleanupWorkRequest = PeriodicWorkRequestBuilder<RecipeCleanupWorker>(
            24, TimeUnit.HOURS  // Repeat interval
        )
            .setConstraints(constraints)
            .build()

        // Schedule the work (replace existing if already scheduled)
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            RecipeCleanupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,  // Keep existing work if already scheduled
            cleanupWorkRequest
        )
    }
}
