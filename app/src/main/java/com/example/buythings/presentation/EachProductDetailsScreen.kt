package com.example.buythings.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.buythings.presentation.ViewModels.ProductViewModel
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.buythings.data.models.CartItem
import com.example.buythings.presentation.ViewModels.CartViewModel


private val CoralPink = Color(0xFFF08080)

@Composable
fun EachProductDetailsScreen(
    productID: String,
    onBackClick: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState
    val cartState = cartViewModel.uiState
    var quantity by remember {
        mutableIntStateOf(1)
    }

    LaunchedEffect(productID) {
        viewModel.getProductById(productID)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        when {

            uiState.isLoading -> {

                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = CoralPink
                )
            }

            uiState.errorMessage != null -> {

                Text(
                    text = uiState.errorMessage ?: "Something went wrong",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.selectedProduct != null -> {

                val product = uiState.selectedProduct!!

                val discountPercentage =
                    if (
                        product.originalPrice > product.price &&
                        product.originalPrice > 0
                    ) {
                        (
                                (product.originalPrice - product.price)
                                        / product.originalPrice * 100
                                ).roundToInt()
                    } else {
                        0
                    }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                    // TOP BAR

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(
                                    MaterialTheme.colorScheme.surface
                                )
                                .clickable {
                                    onBackClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(14.dp)
                        )

                        Text(
                            text = "Product Details",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // PRODUCT IMAGE

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .height(370.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                    ) {

                        if (product.imageUrl.isNotBlank()) {

                            AsyncImage(
                                model = product.imageUrl,
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                        } else {

                            Text(
                                text = "No Image",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        // DISCOUNT BADGE

                        if (discountPercentage > 0) {

                            Box(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CoralPink)
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    )
                            ) {

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        imageVector = Icons.Outlined.LocalOffer,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )

                                    Spacer(
                                        modifier = Modifier.width(4.dp)
                                    )

                                    Text(
                                        text = "$discountPercentage% OFF",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(20.dp)
                    )

                    // PRODUCT INFORMATION

                    Column(
                        modifier = Modifier.padding(
                            horizontal = 20.dp
                        )
                    ) {

                        Text(
                            text = product.name,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        // RATING + STOCK

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.Star,
                                    contentDescription = null,
                                    tint = CoralPink,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(4.dp)
                                )

                                Text(
                                    text = product.rating.toString(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Text(
                                text = "•",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Text(
                                text = if (product.stock > 0) {
                                    "In Stock"
                                } else {
                                    "Out of Stock"
                                },
                                color = if (product.stock > 0) {
                                    Color(0xFF4CAF50)
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        // PRICE

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = "₹${product.price.toInt()}",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 27.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (product.originalPrice > product.price) {

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Text(
                                    text = "₹${product.originalPrice.toInt()}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 15.sp,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        // DESCRIPTION

                        Text(
                            text = "About this product",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = product.description,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        // SIZE

                        Text(
                            text = "Choose your size",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.5.dp,
                                    color = CoralPink,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme.surface
                                )
                                .padding(
                                    horizontal = 22.dp,
                                    vertical = 12.dp
                                )
                        ) {

                            Text(
                                text = product.clothingDetails.size,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        // COLOR

                        Text(
                            text = "Color",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    MaterialTheme.colorScheme.surface
                                )
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 11.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Color.Black
                                    )
                            )

                            Spacer(
                                modifier = Modifier.width(9.dp)
                            )

                            Text(
                                text = product.clothingDetails.color,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )
                        Text(
                            text = "Quantity",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (quantity > 1) {
                                            quantity--
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "−",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 22.sp
                                )
                            }

                            Text(
                                text = quantity.toString(),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 18.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        if (quantity < product.stock) {
                                            quantity++
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 22.sp
                                )
                            }
                        }
                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        val context = LocalContext.current
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CoralPink)
                                .clickable {

                                    Toast.makeText(
                                        context,
                                        "Add to Cart clicked",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val cartItem = CartItem(
                                        productId = product.id,
                                        name = product.name,
                                        imageUrl = product.imageUrl,
                                        priceAtAddTime = product.price,
                                        quantity = quantity,
                                        selectedSize = product.clothingDetails.size,
                                        selectedColor = product.clothingDetails.color
                                    )

                                    cartViewModel.addToCart(cartItem)
                                },
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "Add to Cart",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(15.dp)
                        )

                        cartState.errorMessage?.let { error ->

                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }
                        if (cartState.isLoading) {

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text = "Adding to cart...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }

                        cartState.errorMessage?.let { error ->

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text = "Error: $error",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                        }

                        // PRODUCT INFORMATION CARD

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor =
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {

                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(14.dp)
                            ) {

                                Text(
                                    text = "Product information",
                                    color =
                                        MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                ProductInfoRow(
                                    label = "Material",
                                    value = product.clothingDetails.material
                                )

                                ProductInfoRow(
                                    label = "Available Stock",
                                    value = product.stock.toString()
                                )
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(30.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductInfoRow(
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}