package com.example.buythings.data.models

/**
 * One item inside a user's cart. Lives in Firestore under something like
 * users/{userId}/cart/{cartItemId}
 *
 * We store priceAtAddTime separately from the live product price —
 * this protects against a product's price changing while it's sitting
 * in someone's cart (common real-world e-commerce practice).
 */
data class CartItem(
    val id: String = "",              // set from document ID when reading
    val productId: String = "",       // links back to products/{productId}
    val name: String = "",
    val imageUrl: String = "",
    val priceAtAddTime: Double = 0.0,
    val quantity: Int = 1,
    val selectedSize: String = "",    // relevant for shoes/clothes, blank otherwise
    val selectedColor: String = "",   // relevant for most categories, blank if n/a
    val addedAt: Long = System.currentTimeMillis()
)
