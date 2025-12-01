package com.example.cookingrecipeapp.data

import com.example.cookingrecipeapp.R

val sampleRecipes = listOf(
    Recipe(
        id = 99,
        name = "Spaghetti Carbonara",
        ingredients = "Spaghetti,Eggs,Pancetta,Parmesan Cheese,Black Pepper",
        guide = "Cook spaghetti. In a separate bowl, whisk eggs, cheese, and pepper. Fry pancetta. Combine everything.",
        image = R.drawable.spaghetti_carbonara
    ),
    Recipe(
        id = 100,
        name = "Chicken Tikka Masala",
        ingredients = "Chicken,Yogurt,Tomato Puree,Onion,Garam Masala",
        guide = "Marinate chicken in yogurt and spices. Cook in a tomato-based sauce with onions.",
        image = R.drawable.chicken_tikka_masala
    )
)
