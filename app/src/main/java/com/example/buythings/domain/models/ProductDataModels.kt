package com.example.buythings.data.models

/**
 * One document inside Firestore's "products" collection.
 * Shared fields every product needs, regardless of category.
 *
 * Category-specific attributes (RAM for phones, size for shoes, etc.)
 * live in separate nullable detail objects below — only the one matching
 * this product's category will be non-null.
 */
data class ProductData(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val categoryId: String = "",
    val imageUrl: String = "",
    val stock: Int = 0,
    val rating: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val clothingDetails: ClothingDetails = ClothingDetails()
)
data class ClothingDetails(
    val size: String = "",
    val color: String = "",
    val material: String = ""
)

