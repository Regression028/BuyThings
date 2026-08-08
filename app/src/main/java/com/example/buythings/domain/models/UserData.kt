package com.example.buythings.data.models

/**
 * A user's profile document, stored at users/{userId} in Firestore.
 * The document ID itself IS the Firebase Auth uid — no separate "userId"
 * field needed inside the document.
 *
 * IMPORTANT: no password field here. Firebase Auth already stores
 * passwords securely (hashed) — never duplicate that into Firestore,
 * since Firestore documents can be read by client apps/security rules,
 * and plain-text passwords sitting there would be a real vulnerability.
 */
data class UserData(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileImage: String = "",
    val addresses: List<UserAddress> = emptyList()
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "profileImage" to profileImage,
            "addresses" to addresses
        )
    }
}