package com.example.cookingrecipeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.annotation.DrawableRes

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    @ColumnInfo(name = "ingredients")
    val ingredients: String, // Comma-separated for Room
    val guide: String,
    @DrawableRes val image: Int
)
