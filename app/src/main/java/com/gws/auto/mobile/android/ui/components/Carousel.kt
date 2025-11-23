package com.gws.auto.mobile.android.ui.components

import androidx.compose.ui.res.stringResource
import com.gws.auto.mobile.android.R

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gws.auto.mobile.android.ui.theme.GWSAutoForAndroidTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> AppCarousel(
    items: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { items.size })

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        itemContent(items[page])
    }
}

@Preview(showBackground = true)
@Composable
fun AppCarouselPreview() {
    GWSAutoForAndroidTheme {
        val carouselItems = listOf(Color.Red, Color.Green, Color.Blue)
        Surface(modifier = Modifier.padding(16.dp)) {
            Column {
                Text("Carousel", style = MaterialTheme.typography.headlineSmall)
                AppCarousel(items = carouselItems) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(item)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(R.string.item), style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}
