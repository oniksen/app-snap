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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
private fun AppsListPageContent(
    modifier: Modifier = Modifier,
    appListState: List<AppInfo>,
    appsListIsRefreshing: Pair<Boolean, Float>,
    onItemClick: (AppInfo) -> Unit,
    onRefresh: () -> Unit,
    updateLastScanHash: (packageName: String, hash: String) -> Unit,
) {
    val localDensity = LocalDensity.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = appsListIsRefreshing.first,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = (appsListIsRefreshing.second * 100).roundToInt().toString() + "%",
                    fontWeight = FontWeight.Black,
                )
                CircularWavyProgressIndicator(
                    modifier = Modifier.size(72.dp),
                    progress = { appsListIsRefreshing.second },
                    stroke = with(localDensity) { Stroke(8.dp.toPx()) },
                    trackStroke = with(localDensity) { Stroke(8.dp.toPx()) },
                    gapSize = 4.dp,
                )
            }
        }
        AnimatedVisibility(
            visible = !appsListIsRefreshing.first,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            PullToRefreshLazyColumn(
                modifier = modifier,
                items = appListState,
                getKey = { appInfo -> appInfo.packageName },
                isRefreshing = appsListIsRefreshing.first,
                onRefresh = onRefresh,
            ) {
                var expanded by remember { mutableStateOf(false) }

                Column {
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                Log.d(TAG, "Clicked AppInfo: $it")
                                onItemClick(it)
                            },
                        headlineContent = {
                            Text(text = it.appName)
                        },
                        supportingContent = {
                            Text(
                                text = it.packageName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        leadingContent = {
                            it.iconFilePath?.let { iconPath ->
                                Image(
                                    modifier = Modifier.size(48.dp),
                                    painter = rememberAsyncImagePainter(File(it.iconFilePath)),
                                    contentDescription = "App icon",
                                )
                            }
                        },
                        trailingContent = {
                            if (it.hashSum != it.lastScanHash && it.lastScanHash != null) {
                                IconButton(
                                    onClick = { expanded = !expanded }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error,
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Сохранить изменения") },
                                        onClick = {
                                            updateLastScanHash(
                                                it.packageName,
                                                it.hashSum,
                                            )
                                        }
                                    )
                                }
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