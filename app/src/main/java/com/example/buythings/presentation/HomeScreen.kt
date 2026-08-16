package com.example.buythings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.buythings.data.models.BannerData
import com.example.buythings.data.models.CategoryData
import com.example.buythings.data.models.ProductData
import com.example.buythings.presentation.ViewModels.HomeViewModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val CoralPink = Color(0xFFF08080)

@Composable
fun HomeScreen(
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedBottomItem by remember {
        mutableStateOf(0)
    }

    val uiState = viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.getHomeData()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        bottomBar = {

            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {

                NavigationBarItem(
                    selected = selectedBottomItem == 0,
                    onClick = {
                        selectedBottomItem = 0
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = selectedBottomItem == 1,
                    onClick = {
                        selectedBottomItem = 1
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist"
                        )
                    },
                    label = {
                        Text("Wishlist")
                    }
                )

                NavigationBarItem(
                    selected = selectedBottomItem == 2,
                    onClick = {
                        selectedBottomItem = 2
                        onCartClick()
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart"
                        )
                    },
                    label = {
                        Text("Cart")
                    }
                )

                NavigationBarItem(
                    selected = selectedBottomItem == 3,
                    onClick = {
                        selectedBottomItem = 3
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = {
                        Text("Profile")
                    }
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),

            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 20.dp,
                bottom = 20.dp
            ),

            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // HEADER
            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Welcome to",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "BuyThings",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { }
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.NotificationsNone,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // SEARCH
            item {

                OutlinedTextField(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    placeholder = {
                        Text(
                            text = "Search products...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },

                    leadingIcon = {

                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },

                    singleLine = true,

                    shape = RoundedCornerShape(16.dp),

                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,

                        focusedBorderColor = CoralPink,
                        unfocusedBorderColor = Color.Transparent,

                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,

                        cursorColor = CoralPink
                    )
                )
            }

            // CATEGORIES TITLE
            item {

                SectionHeader(
                    title = "Categories",
                    actionText = "See all",
                    onActionClick = { }
                )
            }

            // CATEGORIES
            item {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(
                        items = uiState.categories
                    ) { category ->

                        CategoryItem(
                            category = category,
                            onClick = { }
                        )
                    }
                }
            }

            // BANNER
            item {

                if (uiState.banners.isNotEmpty()) {

                    BannerCarousel(
                        banners = uiState.banners
                    )

                } else {

                    PromoBanner()
                }
            }

            // FLASH SALE TITLE
            item {

                SectionHeader(
                    title = "Flash Sale",
                    actionText = "See all",
                    onActionClick = { }
                )
            }

            // FLASH SALE
            item {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(
                        items = uiState.products
                    ) { product ->

                        ProductCard(
                            product = product,
                            onClick = {
                                onProductClick(product.id)
                            }
                        )
                    }
                }
            }

            // SUGGESTED TITLE
            item {

                SectionHeader(
                    title = "Suggested For You",
                    actionText = "See all",
                    onActionClick = { }
                )
            }

            // SUGGESTED PRODUCTS
            item {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    items(
                        items = uiState.suggestedProducts
                    ) { product ->

                        ProductCard(
                            product = product,
                            onClick = { onProductClick(product.id)}
                        )
                    }
                }
            }
        }
    }
}


// ================================================================
// SECTION HEADER
// ================================================================

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = actionText,
            color = CoralPink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable {
                onActionClick()
            }
        )
    }
}


// ================================================================
// CATEGORY ITEM
// ================================================================

@Composable
private fun CategoryItem(
    category: CategoryData,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(76.dp)
            .clickable {
                onClick()
            }
    ) {

        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

            if (category.imageUrl.isNotBlank()) {

                AsyncImage(
                    model = category.imageUrl,
                    contentDescription = category.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            } else {

                Text(
                    text = category.name.take(1).uppercase(),
                    color = CoralPink,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.height(7.dp)
        )

        Text(
            text = category.name,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}


// ================================================================
// PROMOTIONAL FALLBACK BANNER
// ================================================================

@Composable
private fun PromoBanner() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(22.dp)
    ) {

        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {

            Text(
                text = "SPECIAL",
                color = CoralPink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Fashion Sale",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Up to 30% OFF",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Shop Now  →",
                color = CoralPink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


// ================================================================
// BANNER CAROUSEL
// ================================================================

@Composable
private fun BannerCarousel(
    banners: List<BannerData>
) {

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            banners.size
        }
    )

    LaunchedEffect(banners.size) {

        while (true) {

            delay(4000)

            if (banners.size > 1) {

                val nextPage =
                    (pagerState.currentPage + 1) % banners.size

                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->

            BannerCard(
                banner = banners[page]
            )
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            banners.forEachIndexed { index, _ ->

                Box(
                    modifier = Modifier
                        .size(
                            if (pagerState.currentPage == index) {
                                18.dp
                            } else {
                                7.dp
                            }
                        )
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) {
                                CoralPink
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                )
            }
        }
    }
}


// ================================================================
// BANNER CARD
// ================================================================

@Composable
private fun BannerCard(
    banner: BannerData
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {

        if (banner.imageUrl.isNotBlank()) {

            AsyncImage(
                model = banner.imageUrl,
                contentDescription = banner.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = banner.title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


// ================================================================
// PRODUCT CARD
// ================================================================

