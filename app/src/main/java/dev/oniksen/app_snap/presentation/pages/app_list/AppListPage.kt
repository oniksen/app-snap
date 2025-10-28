package dev.oniksen.app_snap.presentation.pages.app_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.presentation.pages.app_list.component.AppsListItem
import dev.oniksen.app_snap.presentation.viewmodel.contract.AppsViewModelContract
import dev.oniksen.app_snap.utils.PullToRefreshLazyGrid
import dev.oniksen.app_snap.utils.TestTag
import dev.oniksen.app_snap.utils.previewApps
import kotlin.math.roundToInt

private const val TAG = "AppListPage"

@Composable
fun AppListPage(
    modifier: Modifier = Modifier,
    appsViewModel: AppsViewModelContract,
    onItemClick: (AppInfo) -> Unit,
) {
    val appsListIsRefreshing by appsViewModel.appsListIsRefreshing.collectAsStateWithLifecycle()
    val appListState by appsViewModel.appListState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        appsViewModel.scanIfNeed()
    }

    AppsListPageContent(
        modifier = modifier.fillMaxSize(),
        appListState = appListState,
        appsListIsRefreshing = appsListIsRefreshing,
        onItemClick = onItemClick,
        onRefresh = appsViewModel::rescanApps,
        updateLastScanHash = appsViewModel::updateLastScanHash,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppsListPageContent(
    modifier: Modifier = Modifier,
    appListState: List<AppInfo>,
    appsListIsRefreshing: Pair<Boolean, Float>,
    onItemClick: (AppInfo) -> Unit,
    onRefresh: () -> Unit,
    updateLastScanHash: (packageName: String, hash: String) -> Unit,
) {
    val localDensity = LocalDensity.current
    val (isRefreshing, rawProgress) = appsListIsRefreshing

    val animatedProgress by animateFloatAsState(
        targetValue = if (isRefreshing) rawProgress else 0f,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "ProgressAnimation"
    )

    val animatedPercentage by remember {
        derivedStateOf { (animatedProgress * 100).roundToInt() }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = isRefreshing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "$animatedPercentage%",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                CircularWavyProgressIndicator(
                    modifier = Modifier
                        .size(72.dp)
                        .testTag(TestTag.APP_LIST_PAGE_LOAD_INDICATOR),
                    progress = { animatedProgress }, // Используем анимированный прогресс
                    stroke = with(localDensity) { Stroke(8.dp.toPx()) },
                    trackStroke = with(localDensity) { Stroke(8.dp.toPx()) },
                    gapSize = 4.dp,
                )
            }
        }

        AnimatedVisibility(
            visible = !isRefreshing,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            PullToRefreshLazyGrid(
                modifier = modifier,
                items = appListState,
                getKey = { appInfo -> appInfo.packageName },
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
            ) {
                AppsListItem(
                    appInfo = it,
                    onItemClick = { onItemClick(it) },
                    updateHash = { updateLastScanHash(it.packageName, it.hashSum) }
                )
            }
        }
    }
}

@Composable
private fun StateForPreview(
    appsListIsRefreshing: Pair<Boolean, Float>,
) {
    AppsListPageContent(
        appListState = previewApps,
        appsListIsRefreshing = appsListIsRefreshing,
        onItemClick = {},
        onRefresh = {},
        updateLastScanHash = { _, _ -> },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppsListPageLoading_Light() {
    MaterialExpressiveTheme {
        Surface {
            StateForPreview(Pair(true, 0.3f))
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppsListPageLoading_Dark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            StateForPreview(Pair(true, 0.3f))
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppsListPageMain_Light() {
    MaterialExpressiveTheme {
        Surface {
            StateForPreview(Pair(false, 0.0f))
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppsListPageMain_Dark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            StateForPreview(Pair(false, 0.0f))
        }
    }
}