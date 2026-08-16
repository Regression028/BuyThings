package com.example.buythings.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.buythings.data.models.CartItem
import com.example.buythings.presentation.ViewModels.CartViewModel

private val CoralPink = Color(0xFFF08080)

@Composable
fun CartScreen(
    onBackClick: () -> Unit,
    onCheckoutClick: () -> Unit,
    viewModel: CartViewModel = hiltViewModel()
){

    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getCart()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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

                IconButton(
                    onClick = onBackClick
                ) {

                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Text(
                    text = "My Cart",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            when {

                uiState.isLoading && uiState.cartItems.isEmpty() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        CircularProgressIndicator(
                            color = CoralPink
                        )
                    }
                }

                uiState.errorMessage != null -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = uiState.errorMessage
                                ?: "Something went wrong",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                uiState.cartItems.isEmpty() -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = "Your cart is empty",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text = "Add some products to get started.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                else -> {

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        // CART ITEMS

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            items(
                                items = uiState.cartItems,
                                key = { it.id }
                            ) { cartItem ->

                                CartItemCard(
                                    cartItem = cartItem,
                                    onIncrease = {

                                        viewModel.updateQuantity(
                                            cartItem.id,
                                            cartItem.quantity + 1
                                        )
                                    },
                                    onDecrease = {

                                        viewModel.updateQuantity(
                                            cartItem.id,
                                            cartItem.quantity - 1
                                        )
                                    },
                                    onDelete = {

                                        viewModel.removeFromCart(
                                            cartItem.id
                                        )
                                    }
                                )
                            }
                        }

                        // TOTAL SECTION

                        CartSummary(
                            subtotal = uiState.subtotal,
                            deliveryFee = uiState.deliveryFee,
                            discount = uiState.discount,
                            total = uiState.total,
                            onCheckoutClick = onCheckoutClick
                        )
                    }
                }
            }
        }
        if (uiState.isLoading && uiState.cartItems.isNotEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = CoralPink
                )
            }
        }
    }
}

@Composable
private fun CartItemCard(
    cartItem: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // PRODUCT IMAGE

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {

                if (cartItem.imageUrl.isNotBlank()) {

                    AsyncImage(
                        model = cartItem.imageUrl,
                        contentDescription = cartItem.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                } else {

                    Text(
                        text = "No Image",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            // PRODUCT DETAILS

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = cartItem.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "₹${cartItem.priceAtAddTime.toInt()}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = "Size: ${cartItem.selectedSize}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Text(
                    text = "Color: ${cartItem.selectedColor}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // QUANTITY

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(32.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = "Decrease quantity",
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = cartItem.quantity.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(32.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = "Increase quantity",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // DELETE

            IconButton(
                onClick = onDelete
            ) {

                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Remove from cart",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun CartSummary(
    subtotal: Double,
    deliveryFee: Double,
    discount: Double,
    total: Double,
    onCheckoutClick: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Order Summary",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            SummaryRow(
                label = "Subtotal",
                value = "₹${subtotal.toInt()}"
            )

            SummaryRow(
                label = "Delivery",
                value = "₹${deliveryFee.toInt()}"
            )

            if (discount > 0) {

                SummaryRow(
                    label = "Discount",
                    value = "-₹${discount.toInt()}"
                )
            }

            HorizontalDivider()

            SummaryRow(
                label = "Total",
                value = "₹${total.toInt()}",
                bold = true
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CoralPink)
                    .clickable {
                        onCheckoutClick()
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Proceed to Checkout",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    bold: Boolean = false
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            fontWeight = if (bold) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            }
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = if (bold) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            }
        )
    }
}