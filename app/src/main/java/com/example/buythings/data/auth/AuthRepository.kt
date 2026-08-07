package com.example.buythings.data.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Talks directly to Firebase Auth. Nothing UI-related lives here —
 * just "sign up" and "sign in" operations that either succeed or throw.
 */
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Creates a new Firebase user with email + password.
     * Throws an exception (with a readable message) if it fails —
     * e.g. email already in use, weak password, invalid email, no internet.
     */
    suspend fun signUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }

    /**
     * Signs in an existing user with email + password.
     * Throws an exception if credentials are wrong or user doesn't exist.
     */
    suspend fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    /**
     * Signs the current user out.
     */
    fun logout() {
        firebaseAuth.signOut()
    }

    /**
     * Returns true if a user is currently signed in
     * (useful later for deciding which screen to show on app start).
     */
    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
}