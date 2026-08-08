package com.example.buythings.data.models

/**
 * Matches each document inside Firestore's "categories" collection,
 * e.g. categories/headphones -> { name: "Headphones", imageUrl: "..." }
 *
 * The document ID itself (e.g. "headphones") is used as the category's
 * unique identifier elsewhere in the app (stored separately, not as a field).
 */
data class CategoryData(
    val id: String = "",           // set manually from the document ID when reading, not a Firestore field
    val name: String = "",
    val imageUrl: String = ""
)
