package com.example.buythings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.buythings.data.models.ProductData
import kotlin.math.roundToInt

private val CoralPink = Color(0xFFF08080)

@Composable
fun ProductCard(
    product: ProductData,
    onClick: () -> Unit
) {

    val discountPercentage = if (
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
            .width(175.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                onClick()
            }
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.82f)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
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
                    fontSize = 13.sp
                )
            }

            if (discountPercentage > 0) {

                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CoralPink)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        )
                ) {

                    Text(
                        text = "$discountPercentage% OFF",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(
                text = product.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "₹${product.price.toInt()}",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                if (product.originalPrice > product.price) {

                    Spacer(
                        modifier = Modifier.width(7.dp)
                    )

                    Text(
                        text = "₹${product.originalPrice.toInt()}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "★",
                    color = CoralPink,
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Text(
                    text = product.rating.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}