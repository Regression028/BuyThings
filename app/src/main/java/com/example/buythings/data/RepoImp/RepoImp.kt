package com.example.buythings.data.repo

import android.content.Context
import android.net.Uri
import com.example.buythings.common.ResultState
import com.example.buythings.data.cloudinary.CloudinaryManager
import com.example.buythings.data.models.BannerData
import com.example.buythings.data.models.CartItem
import com.example.buythings.data.models.CategoryData
import com.example.buythings.data.models.ProductData
import com.example.buythings.data.models.UserData
import com.example.buythings.data.models.UserDataParent
import com.example.buythings.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RepoImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : Repo {

    // Creates a Firebase Auth account, then saves the rest of the
    // user's profile info as a Firestore document under the new uid.
    override fun registerUserWithEmailAndPassword(
        userData: UserData,
        password: String
    ): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseAuth.createUserWithEmailAndPassword(userData.email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    val uid = result.result.user?.uid

                    if (uid != null) {
                        firebaseFirestore.collection("users").document(uid)
                            .set(userData)
                            .addOnSuccessListener {
                                trySend(ResultState.Success("User Registered Successfully"))
                                close()
                            }
                            .addOnFailureListener { exception ->
                                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to save user data"))
                                close()
                            }
                    } else {
                        trySend(ResultState.Error("User ID is null"))
                        close()
                    }
                } else {
                    trySend(ResultState.Error(result.exception?.localizedMessage ?: "Registration failed"))
                    close()
                }
            }

        awaitClose()
    }

    // Signs an existing user in with email and password.
    override fun loginUserWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    trySend(ResultState.Success("Login Successful"))
                } else {
                    trySend(ResultState.Error(result.exception?.localizedMessage ?: "Login failed"))
                }
                close()
            }

        awaitClose()
    }

    // Fetches one user's profile document by uid, paired with its document ID.
    override fun getUserById(uid: String): Flow<ResultState<UserDataParent>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val data = document.toObject(UserData::class.java)
                    if (data != null) {
                        trySend(ResultState.Success(UserDataParent(document.id, data)))
                    } else {
                        trySend(ResultState.Error("Unable to convert user data"))
                    }
                } else {
                    trySend(ResultState.Error("User not found"))
                }
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get user"))
                close()
            }

        awaitClose()
    }

    // Overwrites the currently logged-in user's profile document.
    override fun updateUserData(userData: UserData): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        firebaseFirestore.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                trySend(ResultState.Success("User Data Updated Successfully"))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to update user"))
                close()
            }

        awaitClose()
    }

    // Uploads a profile photo via Cloudinary, then saves the resulting
    // URL onto the user's Firestore document.
    override fun userProfileImage(uri: Uri): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        try {
            val result = CloudinaryManager.uploadImage(context, uri.toString())
            val imageUrl = result.secureUrl

            firebaseFirestore.collection("users").document(uid)
                .update("profileImage", imageUrl)
                .addOnSuccessListener {
                    trySend(ResultState.Success(imageUrl))
                    close()
                }
                .addOnFailureListener { exception ->
                    trySend(ResultState.Error(exception.localizedMessage ?: "Failed to save image URL"))
                    close()
                }
        } catch (e: Exception) {
            trySend(ResultState.Error(e.message ?: "Image upload failed"))
            close()
        }

        awaitClose()
    }

    // Fetches a small batch of categories, for the Home screen row.
    override fun getCategoriesInLimited(): Flow<ResultState<List<CategoryData>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("categories").limit(7).get()
            .addOnSuccessListener { snapshot ->
                val categories = snapshot.documents.mapNotNull { document ->
                    document.toObject(CategoryData::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(categories))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get categories"))
                close()
            }

        awaitClose()
    }

    // Fetches a small batch of products, for the Home screen sections.
    override fun getProductsInLimited(): Flow<ResultState<List<ProductData>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("products").limit(10).get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { document ->
                    document.toObject(ProductData::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(products))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get products"))
                close()
            }

        awaitClose()
    }

    // Fetches every product, for the All Products screen.
    override fun getAllProducts(): Flow<ResultState<List<ProductData>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("products").get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { document ->
                    document.toObject(ProductData::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(products))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get products"))
                close()
            }

        awaitClose()
    }

    // Fetches one product by its document ID, for the Product Details screen.
    override fun getProductById(productId: String): Flow<ResultState<ProductData>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                val product = document.toObject(ProductData::class.java)?.copy(id = document.id)
                if (product != null) {
                    trySend(ResultState.Success(product))
                } else {
                    trySend(ResultState.Error("Product not found"))
                }
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get product"))
                close()
            }

        awaitClose()
    }

    // Adds an item to the logged-in user's cart subcollection.
    override fun addToCart(cartItem: CartItem): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        firebaseFirestore.collection("ADD_TO_CART").document(uid)
            .collection("User_Cart").add(cartItem)
            .addOnSuccessListener {
                trySend(ResultState.Success("Product Added To Cart"))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to add product to cart"))
                close()
            }

        awaitClose()
    }

    // Adds a product to the logged-in user's favorites subcollection.
    override fun addToFav(productData: ProductData): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        firebaseFirestore.collection("ADD_TO_FAV").document(uid)
            .collection("User_Fav").add(productData)
            .addOnSuccessListener {
                trySend(ResultState.Success("Product Added To Favourite"))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to add favourite"))
                close()
            }

        awaitClose()
    }

    // Fetches every product the logged-in user has favorited.
    override fun getAllFav(): Flow<ResultState<List<ProductData>>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        firebaseFirestore.collection("ADD_TO_FAV").document(uid)
            .collection("User_Fav").get()
            .addOnSuccessListener { snapshot ->
                val favourites = snapshot.documents.mapNotNull { document ->
                    document.toObject(ProductData::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(favourites))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get favourites"))
                close()
            }

        awaitClose()
    }

    // Fetches everything currently in the logged-in user's cart.
    override fun getCart(): Flow<ResultState<List<CartItem>>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        firebaseFirestore.collection("ADD_TO_CART").document(uid)
            .collection("User_Cart").get()
            .addOnSuccessListener { snapshot ->
                val cart = snapshot.documents.mapNotNull { document ->
                    document.toObject(CartItem::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(cart))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get cart"))
                close()
            }

        awaitClose()
    }

    // Fetches every category, for the All Categories screen.
    override fun getAllCategories(): Flow<ResultState<List<CategoryData>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("categories").get()
            .addOnSuccessListener { snapshot ->
                val categories = snapshot.documents.mapNotNull { document ->
                    document.toObject(CategoryData::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(categories))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get categories"))
                close()
            }

        awaitClose()
    }

    // Fetches the cart contents again for the checkout review screen.
    override fun getCheckOut(): Flow<ResultState<List<CartItem>>> = callbackFlow {
        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        firebaseFirestore.collection("ADD_TO_CART").document(uid)
            .collection("User_Cart").get()
            .addOnSuccessListener { snapshot ->
                val checkoutItems = snapshot.documents.mapNotNull { document ->
                    document.toObject(CartItem::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(checkoutItems))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get checkout items"))
                close()
            }

        awaitClose()
    }

    // Fetches every product belonging to one category.
    override fun getSpecificCategoryItems(categoryName: String): Flow<ResultState<List<ProductData>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("products")
            .whereEqualTo("categoryId", categoryName)
            .get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { document ->
                    document.toObject(ProductData::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(products))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get category products"))
                close()
            }

        awaitClose()
    }

    // Fetches a batch of suggested products for the Home screen.
    override fun getAllSuggestedProducts(): Flow<ResultState<List<ProductData>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("products").limit(10).get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.documents.mapNotNull { document ->
                    document.toObject(ProductData::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(products))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get suggested products"))
                close()
            }

        awaitClose()
    }

    // Fetches every promotional banner, for the Home screen carousel.
    override fun getBanner(): Flow<ResultState<List<BannerData>>> = callbackFlow {
        trySend(ResultState.Loading)

        firebaseFirestore.collection("banners").get()
            .addOnSuccessListener { snapshot ->
                val banners = snapshot.documents.mapNotNull { document ->
                    document.toObject(BannerData::class.java)?.copy(id = document.id)
                }
                trySend(ResultState.Success(banners))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(ResultState.Error(exception.localizedMessage ?: "Failed to get banners"))
                close()
            }

        awaitClose()
    }
    override fun updateCartQuantity(
        cartItemId: String,
        quantity: Int
    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        if (quantity <= 0) {
            trySend(ResultState.Error("Quantity must be greater than 0"))
            close()
            return@callbackFlow
        }

        firebaseFirestore
            .collection("ADD_TO_CART")
            .document(uid)
            .collection("User_Cart")
            .document(cartItemId)
            .update("quantity", quantity)
            .addOnSuccessListener {
                trySend(ResultState.Success("Cart quantity updated"))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(
                    ResultState.Error(
                        exception.localizedMessage
                            ?: "Failed to update cart quantity"
                    )
                )
                close()
            }

        awaitClose()
    }

    override fun removeFromCart(
        cartItemId: String
    ): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        firebaseFirestore
            .collection("ADD_TO_CART")
            .document(uid)
            .collection("User_Cart")
            .document(cartItemId)
            .delete()
            .addOnSuccessListener {
                trySend(ResultState.Success("Product removed from cart"))
                close()
            }
            .addOnFailureListener { exception ->
                trySend(
                    ResultState.Error(
                        exception.localizedMessage
                            ?: "Failed to remove product from cart"
                    )
                )
                close()
            }

        awaitClose()
    }
    override fun clearCart(): Flow<ResultState<String>> = callbackFlow {

        trySend(ResultState.Loading)

        val uid = firebaseAuth.currentUser?.uid

        if (uid == null) {
            trySend(ResultState.Error("User is not logged in"))
            close()
            return@callbackFlow
        }

        firebaseFirestore
            .collection("ADD_TO_CART")
            .document(uid)
            .collection("User_Cart")
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {
                    trySend(ResultState.Success("Cart is already empty"))
                    close()
                    return@addOnSuccessListener
                }

                val batch = firebaseFirestore.batch()

                snapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        trySend(ResultState.Success("Cart cleared successfully"))
                        close()
                    }
                    .addOnFailureListener { exception ->
                        trySend(
                            ResultState.Error(
                                exception.localizedMessage
                                    ?: "Failed to clear cart"
                            )
                        )
                        close()
                    }
            }
            .addOnFailureListener { exception ->
                trySend(
                    ResultState.Error(
                        exception.localizedMessage
                            ?: "Failed to get cart items"
                    )
                )
                close()
            }

        awaitClose()
    }
}