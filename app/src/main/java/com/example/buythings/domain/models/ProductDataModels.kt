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
    val id: String = "",                 // set from document ID when reading
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val categoryId: String = "",         // matches a document ID in "categories", e.g. "mobiles"
    val imageUrl: String = "",
    val stock: Int = 0,
    val rating: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),

    // Only ONE of these will be non-null, matching categoryId
    val phoneDetails: PhoneDetails? = null,
    val shoeDetails: ShoeDetails? = null,
    val clothingDetails: ClothingDetails? = null,
    val headphoneDetails: HeadphoneDetails? = null
)

data class PhoneDetails(
    val ram: String = "",
    val storage: String = "",
    val color: String = "",
    val batteryCapacity: String = ""
)

data class ShoeDetails(
    val size: String = "",
    val color: String = "",
    val material: String = ""
)

data class ClothingDetails(
    val size: String = "",
    val color: String = "",
    val material: String = ""
)

data class HeadphoneDetails(
    val color: String = "",
    val connectivity: String = "",       // "Wired" or "Wireless"
    val batteryLife: String = ""
)