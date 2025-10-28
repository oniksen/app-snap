package dev.oniksen.app_snap.presentation.pages.app_list.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.utils.appDetailsParams
import dev.oniksen.app_snap.utils.previewApps
import java.io.File

private const val TAG = "AppsListItem"

@Composable
fun AppsListItem(
    appInfo: AppInfo,
    onItemClick: () -> Unit,
    updateHash: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        ListItem(
            modifier = Modifier
                .clickable {
                    Log.d(TAG, "Clicked AppInfo: $appInfo")
                    onItemClick()
                },
            headlineContent = {
                Text(text = appInfo.appName)
            },
            supportingContent = {
                Text(
                    text = appInfo.packageName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingContent = {
                appInfo.iconFilePath?.let { iconPath ->
                    Image(
                        modifier = Modifier.size(48.dp),
                        painter = rememberAsyncImagePainter(File(appInfo.iconFilePath)),
                        contentDescription = "App icon",
                    )
                }
            },
            trailingContent = {
                if (appInfo.hashSum != appInfo.lastScanHash && appInfo.lastScanHash != null) {
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
                            onClick = updateHash,
                        )
                    }
                }
            }
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
fun StateForPreview() {
    AppsListItem(
        appInfo = previewApps.random(),
        onItemClick = { },
        updateHash = { },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppsListItem_Light() {
    MaterialExpressiveTheme {
        Surface {
            StateForPreview()
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppsListItem_Dark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            StateForPreview()
        }
    }
}