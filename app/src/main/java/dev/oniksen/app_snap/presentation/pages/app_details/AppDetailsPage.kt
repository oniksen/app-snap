package dev.oniksen.app_snap.presentation.pages.app_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.presentation.theme.displayFontFamily
import dev.oniksen.app_snap.presentation.viewmodel.contract.AppsViewModelContract
import dev.oniksen.app_snap.utils.previewApps
import java.io.File

@Composable
fun AppDetailsPage(
    modifier: Modifier = Modifier,
    packageName: String,
    appsViewModel: AppsViewModelContract,
    updateLastScanHash: (packageName: String, hash: String) -> Unit,
) {
    val appInfo by appsViewModel.fetchAppInfo(packageName).collectAsStateWithLifecycle(null)

    if (appInfo != null) {
        AppDetailsPageContent(
            modifier = modifier,
            appInfo = appInfo!!,
            updateLastScanHash = updateLastScanHash,
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text("Загрузка...")
        }
    }
}

@Composable
private fun AppDetailsPageContent (
    modifier: Modifier = Modifier,
    appInfo: AppInfo,
    updateLastScanHash: (packageName: String, hash: String) -> Unit,
) {
    val isAppModified = appInfo.hashSum != appInfo.lastScanHash && appInfo.lastScanHash != null
    val localContext = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Header(
            modifier = Modifier.padding(bottom = 24.dp),
            iconFilePath = appInfo.iconFilePath.toString(),
            appName = appInfo.appName,
            packageName = appInfo.packageName
        ) {
            val launchIntent = localContext.packageManager.getLaunchIntentForPackage(appInfo.packageName)
            if (launchIntent != null) {
                localContext.startActivity(launchIntent)
            }
        }

        if (isAppModified) {
            ModifiedAppAlertFIeld(modifier = Modifier.padding(bottom = 8.dp))
        }

        VersionField(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            value = appInfo.appVersion ?: "Неизвестно",
        )

        HashSumField(
            modifier = Modifier,
            hashSum = appInfo.hashSum,
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (isAppModified)
                UpdateHashButton {
                    updateLastScanHash(
                        appInfo.packageName,
                        appInfo.hashSum,
                    )
                }
        }
    }
}

@Composable
private fun Header(
    modifier: Modifier = Modifier,
    iconFilePath: String,
    appName: String,
    packageName: String,
    openApp: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically // КЛЮЧЕВОЕ: центрируем иконку по вертикали
    ) {
        Image(
            modifier = Modifier
                .weight(0.6f, fill = false) // 25% ширины, но не растягивается
                .aspectRatio(1f),
            painter = rememberAsyncImagePainter(File(iconFilePath)),
            contentDescription = "Иконка приложения",
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Правая колонка — растягивается по содержимому
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Column {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    fontFamily = displayFontFamily,
                )
                Text(
                    text = packageName,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // ФИКСИРОВАННЫЙ ОТСТУП

            Button(
                onClick = openApp,
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text("Открыть")
            }
        }
    }
}

@Composable
private fun VersionField(modifier: Modifier = Modifier, value: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        FlowRow (
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Версия",
                color = MaterialTheme.colorScheme.outline,
            )
            Text(value)
        }
    }
}

@Composable
private fun HashSumField (
    modifier: Modifier = Modifier,
    hashSum: String,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Контрольная сумма (sha-256)",
                color = MaterialTheme.colorScheme.outline,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = hashSum,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ModifiedAppAlertFIeld(
    modifier: Modifier = Modifier,
) {
    Row {
        Card(
            modifier = modifier
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Приложение модифицировано"
                )
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UpdateHashButton(
    modifier: Modifier = Modifier,
    updateLastScanHash: () -> Unit
) {
    OutlinedButton(
        modifier = modifier.wrapContentSize(),
        onClick = updateLastScanHash,
    ) {
        Row(
            modifier = Modifier.padding(ButtonDefaults.MediumContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.
                    padding(end = ButtonDefaults.MediumIconSpacing),
                imageVector = Icons.Outlined.Save,
                contentDescription = null,
            )
            Text(
                text = "Сохранить изменения"
            )
        }
    }
}

@Composable
private fun StateForPreview() {
    AppDetailsPageContent(appInfo = previewApps.first()) { _, _ ->}
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppDetailsPage_Light() {
    MaterialExpressiveTheme {
        Surface {
            StateForPreview()
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun AppDetailsPage_Dark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            StateForPreview()
        }
    }
}