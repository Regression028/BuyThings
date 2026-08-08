package com.example.buythings.data.models

/**
 * A promotional banner shown in a carousel on the Home screen.
 * Lives in Firestore under a "banners" collection.
 *
 * Tapping a banner can link to either a specific product OR a whole
 * category (e.g. "50% off Headphones" banner links to categoryId
 * instead of a single productId) — only one of these two should be set.
 */
data class BannerData(
    val id: String = "",              // set from document ID when reading
    val imageUrl: String = "",
    val title: String = "",
    val linkedProductId: String = "",
    val linkedCategoryId: String = "",
    val displayOrder: Int = 0         // controls carousel ordering, lower = shown first
)