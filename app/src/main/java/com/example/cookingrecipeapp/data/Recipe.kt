package com.example.cookingrecipeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.annotation.DrawableRes

/**
 * Recipe data model representing a recipe entity in the database
 * Supports both drawable resource images and custom camera-captured images
 */
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "ingredients")
    val ingredients: String, // Comma-separated for Room
    val guide: String,
    @DrawableRes val image: Int, // Drawable resource ID
    val imagePath: String? = null, // File path for camera-captured images
    val isFavorite: Boolean = false,
    val isViewed: Boolean = false
)
