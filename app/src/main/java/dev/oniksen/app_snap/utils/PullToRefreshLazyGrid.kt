package dev.oniksen.app_snap.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun<T> PullToRefreshLazyGrid(
    modifier: Modifier = Modifier,
    items: List<T>,
    getKey: (T) -> Any,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    listItemContent: @Composable (T) -> Unit,
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
        state = rememberPullToRefreshState(),
    ) {
        LazyVerticalGrid(
            modifier = Modifier.testTag(TestTag.APP_LIST_PAGE_ITEMS_GRID),
            columns = GridCells.Adaptive(minSize = 300.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(items, key = { getKey(it) }) {
                listItemContent(it)
            }
        }
    }
}