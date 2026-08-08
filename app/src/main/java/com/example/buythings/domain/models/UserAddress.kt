package com.example.buythings.data.models

/**
 * A single saved delivery address. A user can have multiple
 * (home, work, etc.) — stored as a list inside UserData, or as a
 * subcollection under users/{userId}/addresses if the list grows large.
 */
data class UserAddress(
    val id: String = "",
    val label: String = "",           // "Home", "Work", etc.
    val addressLine1: String = "",
    val addressLine2: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val phoneNumber: String = "",
    val isDefault: Boolean = false
)
