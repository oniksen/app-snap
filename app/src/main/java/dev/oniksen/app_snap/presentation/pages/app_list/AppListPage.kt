package dev.oniksen.app_snap.presentation.pages.app_list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.utils.PullToRefreshLazyColumn
import dev.oniksen.app_snap.utils.previewApps
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AppListPage(
    modifier: Modifier = Modifier,
    apps: List<AppInfo>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {

    PullToRefreshLazyColumn(
        modifier = modifier,
        items = apps,
        getKey = { appInfo -> appInfo.packageName },
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
    ) {
        ListItem(
            headlineContent = {
                Text(text = it.appName)
            },
            supportingContent = {
                Text(text = it.packageName)
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
    }
}

@Composable
private fun StateForPreview() {

    AppListPage(
        apps = previewApps,
        modifier = Modifier,
        isRefreshing = false,
        onRefresh = { },
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