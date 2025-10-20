package dev.oniksen.app_snap.presentation.pages.app_list

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.utils.previewApps
import java.io.File

@Composable
fun AppListPage(
    modifier: Modifier = Modifier,
    apps: List<AppInfo>,
) {

    LazyColumn(modifier = modifier) {
        items(apps, key = { appInfo -> appInfo.uuid }) { appInfo ->
            ListItem(
                headlineContent = {
                    Text(text = appInfo.appName)
                },
                supportingContent = {
                    Text(text = appInfo.packageName)
                },
                leadingContent = {
                    appInfo.iconFilePath?.let { iconPath ->
                        Image(
                            modifier = Modifier.size(48.dp),
                            painter = rememberAsyncImagePainter(File(appInfo.iconFilePath)),
                            contentDescription = "App icon",
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun StateForPreview() {

    AppListPage(
        apps = previewApps,
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