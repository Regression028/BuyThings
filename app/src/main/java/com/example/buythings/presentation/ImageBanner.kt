package com.example.buythings.presentation.Utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay

data class BannerData(
    val name: String,
    val imageUrl: String
)

@Composable
fun Banner(
    banners: List<BannerData>
) {

    if (banners.isEmpty()) return

    val pagerState = rememberPagerState(
        pageCount = { banners.size }
    )

    // Automatically move to the next banner
    LaunchedEffect(Unit) {

        while (true) {

            delay(1500)

            val nextPage =
                (pagerState.currentPage + 1) % banners.size

            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Banner images
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { currentPage ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(
                        start = 4.dp,
                        end = 4.dp,
                        top = 8.dp
                    ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {

                AsyncImage(
                    model = banners[currentPage].imageUrl,
                    contentDescription = banners[currentPage].name,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center
                )
            }
        }

        // Dots
        PageIndicator(
            pageCount = banners.size,
            currentPage = pagerState.currentPage
        )
    }
}


@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int
) {

    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        repeat(pageCount) { page ->

            if (page == currentPage) {

                // Selected dot
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(20.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFF08080))
                )

            } else {

                // Normal dot
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF888888))
                )
            }
        }
    }
}