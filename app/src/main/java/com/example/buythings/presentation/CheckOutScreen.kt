package com.example.buythings.presentation

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.buythings.data.models.UserAddress
import com.example.buythings.data.payment.PaymentManager
import com.example.buythings.data.payment.PaymentResult
import com.example.buythings.presentation.ViewModels.CartViewModel
import com.example.buythings.presentation.ViewModels.UserViewModel
import com.razorpay.Checkout
import org.json.JSONObject

private val CoralPink = Color(0xFFF08080)

@Composable
fun CheckoutScreen(
    onBackClick: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val cartState = cartViewModel.uiState
    val userState = userViewModel.uiState

    var showAddAddressDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var currentOrderId by remember { mutableStateOf<String?>(null) }

    var addressLabel by remember { mutableStateOf("") }
    var addressLine1 by remember { mutableStateOf("") }
    var addressLine2 by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        cartViewModel.getCart()
        userViewModel.getUserData()
    }

    // 1. Listen for Razorpay Order response -> Open Razorpay Checkout Sheet
    LaunchedEffect(cartState.razorpayOrderResponse) {
        val orderResponse = cartState.razorpayOrderResponse
        if (orderResponse != null && orderResponse.success && orderResponse.order != null) {
            val keyId = orderResponse.keyId
            val orderId = orderResponse.order.id
            currentOrderId = orderId

            if (!keyId.isNullOrBlank()) {
                val activity = context as? Activity
                if (activity != null) {
                    val checkout = Checkout()
                    checkout.setKeyID(keyId)

                    try {
                        val options = JSONObject().apply {
                            put("name", "BuyThings")
                            put("description", "Purchase Payment")
                            put("theme.color", "#FF8A98")
                            put("currency", orderResponse.order.currency)
                            put("amount", orderResponse.order.amount)
                            put("order_id", orderId)

                            val userEmail = userState.userData?.email ?: ""
                            val userPhone = phoneNumber.ifBlank {
                                userState.userData?.phoneNumber ?: ""
                            }

                            val prefill = JSONObject().apply {
                                if (userEmail.isNotBlank()) put("email", userEmail)
                                if (userPhone.isNotBlank()) put("contact", userPhone)
                            }
                            put("prefill", prefill)
                        }

                        checkout.open(activity, options)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error launching Razorpay: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Razorpay Key ID is missing from server response", Toast.LENGTH_LONG).show()
            }
            cartViewModel.consumeRazorpayOrderResponse()
        }
    }

    // 2. Listen for Razorpay Payment callback results
    LaunchedEffect(Unit) {
        PaymentManager.paymentResult.collect { result ->
            when (result) {
                is PaymentResult.Success -> {
                    val orderId = currentOrderId
                    val paymentId = result.paymentId
                    val paymentData = result.paymentData
                    val signature = paymentData?.signature ?: ""

                    if (orderId != null && paymentId != null) {
                        cartViewModel.verifyPayment(
                            orderId = orderId,
                            paymentId = paymentId,
                            signature = signature
                        )
                    } else {
                        Toast.makeText(context, "Payment Successful! ID: $paymentId", Toast.LENGTH_LONG).show()
                    }
                }
                is PaymentResult.Error -> {
                    val msg = if (result.errorCode == Checkout.PAYMENT_CANCELED) {
                        "Payment Cancelled"
                    } else {
                        result.response ?: "Payment Failed"
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // 3. React to Payment Verification Success
    LaunchedEffect(cartState.isPaymentVerified) {
        if (cartState.isPaymentVerified) {
            showSuccessDialog = true
        }
    }

    // 4. React to Payment Error Messages
    LaunchedEffect(cartState.paymentErrorMessage) {
        cartState.paymentErrorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
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
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

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
                            containerColor = MaterialTheme.colorScheme.surface
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
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
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
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = cartItem.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

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

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Qty: ${cartItem.quantity}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = "₹${(cartItem.priceAtAddTime * cartItem.quantity).toInt()}",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "Delivery Address",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (userState.userData?.addresses?.isNotEmpty() == true) {
                    items(
                        items = userState.userData!!.addresses,
                        key = { it.id }
                    ) { address ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Address selection
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = address.label.uppercase(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = address.addressLine1,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                if (address.addressLine2.isNotBlank()) {
                                    Text(
                                        text = address.addressLine2,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 14.sp
                                    )
                                }

                                Text(
                                    text = "${address.city}, ${address.state} - ${address.pincode}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Phone: ${address.phoneNumber}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "No delivery address saved",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Add an address to continue.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                // ADD NEW ADDRESS BUTTON
                item {
                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CoralPink)
                            .clickable {
                                showAddAddressDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ Add New Address",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Price Details",
                                color = MaterialTheme.colorScheme.onSurface,
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

                            HorizontalDivider()

                            CheckoutSummaryRow(
                                label = "Total",
                                value = "₹${cartState.total.toInt()}",
                                bold = true
                            )
                        }
                    }
                }

                // PAY WITH RAZORPAY BUTTON
                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (cartState.cartItems.isEmpty()) {
                                Toast.makeText(context, "Your cart is empty", Toast.LENGTH_SHORT).show()
                            } else if (cartState.total <= 0) {
                                Toast.makeText(context, "Invalid order total", Toast.LENGTH_SHORT).show()
                            } else {
                                cartViewModel.createPaymentOrder(cartState.total)
                            }
                        },
                        enabled = !cartState.isPaymentLoading && cartState.cartItems.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CoralPink
                        )
                    ) {
                        if (cartState.isPaymentLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Pay with Razorpay (₹${cartState.total.toInt()})",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text(text = "Add New Address", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = addressLabel,
                        onValueChange = { addressLabel = it },
                        label = { Text("Label (Home, Work)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = addressLine1,
                        onValueChange = { addressLine1 = it },
                        label = { Text("Address Line 1") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = addressLine2,
                        onValueChange = { addressLine2 = it },
                        label = { Text("Address Line 2") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = { Text("Pincode") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Phone Number") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newAddress = UserAddress(
                            id = System.currentTimeMillis().toString(),
                            label = addressLabel,
                            addressLine1 = addressLine1,
                            addressLine2 = addressLine2,
                            city = city,
                            state = state,
                            pincode = pincode,
                            phoneNumber = phoneNumber,
                            isDefault = false
                        )
                        userViewModel.addAddress(newAddress)
                        showAddAddressDialog = false

                        addressLabel = ""
                        addressLine1 = ""
                        addressLine2 = ""
                        city = ""
                        state = ""
                        pincode = ""
                        phoneNumber = ""
                    }
                ) {
                    Text("Save Address")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                cartViewModel.resetPaymentState()
                onBackClick()
            },
            title = {
                Text(
                    text = "Order Placed Successfully! 🎉",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Thank you for your purchase!\n\nYour payment ID is ${cartState.verifiedPaymentId ?: "confirmed"}.\nYour order is now being processed."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        cartViewModel.resetPaymentState()
                        onBackClick()
                    }
                ) {
                    Text("Back to Home")
                }
            }
        )
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
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium
        )
    }
}
