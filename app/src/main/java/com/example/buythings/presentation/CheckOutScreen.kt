package com.example.buythings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.buythings.presentation.ViewModels.CartViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.buythings.presentation.ViewModels.UserViewModel

private val CoralPink = Color(0xFFF08080)

@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {

    val cartState = cartViewModel.uiState
    val userState = userViewModel.uiState

    LaunchedEffect(Unit) {

        cartViewModel.getCart()
        userViewModel.getUserData()
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

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = "Checkout",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 16.dp,
                    vertical = 10.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {

                    Text(
                        text = "Order Summary",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(
                    items = cartState.cartItems,
                    key = { it.id }
                ) { cartItem ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .height(85.dp)
                                    .width(75.dp)
                                    .clip(
                                        RoundedCornerShape(12.dp)
                                    )
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
                                        modifier = Modifier.align(
                                            Alignment.Center
                                        ),
                                        color = MaterialTheme.colorScheme
                                            .onSurfaceVariant,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = cartItem.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(
                                    modifier = Modifier.height(4.dp)
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
                                    modifier = Modifier.height(4.dp)
                                )

                                Text(
                                    text = "Qty: ${cartItem.quantity}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = "₹${
                                    (
                                            cartItem.priceAtAddTime *
                                                    cartItem.quantity
                                            ).toInt()
                                }",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

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
                                Arrangement.spacedBy(12.dp)
                        ) {

                            Text(
                                text = "Price Details",
                                color =
                                    MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            CheckoutSummaryRow(
                                label = "Subtotal",
                                value = "₹${cartState.subtotal.toInt()}"
                            )

                            CheckoutSummaryRow(
                                label = "Delivery",
                                value = "₹${cartState.deliveryFee.toInt()}"
                            )

                            if (cartState.discount > 0) {

                                CheckoutSummaryRow(
                                    label = "Discount",
                                    value = "-₹${cartState.discount.toInt()}"
                                )
                            }

                            androidx.compose.material3.HorizontalDivider()

                            CheckoutSummaryRow(
                                label = "Total",
                                value = "₹${cartState.total.toInt()}",
                                bold = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutSummaryRow(
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