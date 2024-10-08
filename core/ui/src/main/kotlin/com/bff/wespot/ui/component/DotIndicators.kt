package com.bff.wespot.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DotIndicators(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth(),
    ) {
        repeat(pagerState.pageCount) {
            val color = if (pagerState.currentPage == it) {
                Color(0xFFF7F7F8)
            } else {
                Color(0xFF838383)
            }

            Box(
                modifier = modifier
                    .clip(CircleShape)
                    .background(color)
                    .size(7.dp),
            )
            if (it < pagerState.pageCount - 1) {
                Spacer(modifier = Modifier.width(14.dp))
            }
        }
    }
}
