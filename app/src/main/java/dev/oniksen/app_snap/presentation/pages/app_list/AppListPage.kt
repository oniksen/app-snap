package dev.oniksen.app_snap.presentation.pages.app_list

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.presentation.viewmodel.contract.AppsViewModelContract
import dev.oniksen.app_snap.utils.PullToRefreshLazyColumn
import dev.oniksen.app_snap.utils.previewApps
import java.io.File
import kotlin.math.roundToInt

private const val TAG = "AppListPage"

@Composable
fun AppListPage(
    modifier: Modifier = Modifier,
    appsViewModel: AppsViewModelContract,
    onItemClick: (AppInfo) -> Unit,
) {
    val appsListIsrefreshing by appsViewModel.appsListIsRefreshing.collectAsStateWithLifecycle()
    val appListState by appsViewModel.appListState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        appsViewModel.scanIfNeed()
    }
    LaunchedEffect(appsListIsrefreshing) {
        Log.d(TAG, "appsListIsrefreshing: ${appsListIsrefreshing.second}")
    }

    AppsListPageContent(
        modifier = modifier.fillMaxSize(),
        appListState = appListState,
        appsListIsrefreshing = appsListIsrefreshing,
        onItemClick = onItemClick,
        onRefresh = { appsViewModel.rescanApps() }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppsListPageContent(
    modifier: Modifier = Modifier,
    appListState: List<AppInfo>,
    appsListIsrefreshing: Pair<Boolean, Float>,
    onItemClick: (AppInfo) -> Unit,
    onRefresh: () -> Unit,
) {
    val localDensity = LocalDensity.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = appsListIsrefreshing.first,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = (appsListIsrefreshing.second * 100).roundToInt().toString() + "%",
                    fontWeight = FontWeight.Black,
                )
                CircularWavyProgressIndicator(
                    modifier = Modifier.size(72.dp),
                    progress = { appsListIsrefreshing.second },
                    stroke = with(localDensity) { Stroke(8.dp.toPx()) },
                    trackStroke = with(localDensity) { Stroke(8.dp.toPx()) },
                    gapSize = 4.dp,
                )
            }
        }
        AnimatedVisibility(
            visible = !appsListIsrefreshing.first,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            PullToRefreshLazyColumn(
                modifier = modifier,
                items = appListState,
                getKey = { appInfo -> appInfo.packageName },
                isRefreshing = appsListIsrefreshing.first,
                onRefresh = onRefresh,
            ) {
                Column {
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                onItemClick(it)
                            },
                        headlineContent = {
                            Text(text = it.appName)
                        },
                        supportingContent = {
                            Column {
                                Text(text = it.packageName)
                                if (it.hashSum != it.lastScanHash && it.lastScanHash != null) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = "Изменено",
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.End,
                                    )
                                }
                            }
                        },
                        leadingContent = {
                            it.iconFilePath?.let { iconPath ->
                                Image(
                                    modifier = Modifier.size(48.dp),
                                    painter = rememberAsyncImagePainter(File(it.iconFilePath)),
                                    contentDescription = "App icon",
                                )
                            }
                        }
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun StateForPreview() {
    AppsListPageContent (
        appListState = previewApps,
        appsListIsrefreshing = Pair(true, 0.3f),
        onItemClick = {},
        onRefresh = {},
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppsListPage_Light() {
    MaterialExpressiveTheme {
        Surface {
            StateForPreview()
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppsListPage_Dark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            StateForPreview()
        }
    }
}