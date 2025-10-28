package dev.oniksen.app_snap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import dev.oniksen.app_snap.presentation.pages.app_list.AppsListPageContent
import dev.oniksen.app_snap.utils.TestTag
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AppsListPageContentTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    private var refreshingState by mutableStateOf(Pair(false, 0f))

    @Before
    fun setUp() {
        composeTestRule.setContent {
            AppsListPageContent(
                appListState = emptyList(),
                appsListIsRefreshing = refreshingState,
                onItemClick = { },
                onRefresh = { },
                updateLastScanHash = { _,_ -> }
            )
        }
    }

    @Test
    fun testPullToRefreshIndicatorVisibleWhenRefreshing() {
        // 1. Устанавливаем начальное состояние: загружаем список на 30%.
        refreshingState = Pair(true, 0.3f)

        // 2. Проверка видимости индикатора загрузки.
        composeTestRule.onNodeWithTag(TestTag.APP_LIST_PAGE_LOAD_INDICATOR).assertIsDisplayed()

        // 3. Проверяем скрытие Grid с существующими элементами.
        composeTestRule.onNodeWithTag(TestTag.APP_LIST_PAGE_ITEMS_GRID).assertIsNotDisplayed()
    }
}