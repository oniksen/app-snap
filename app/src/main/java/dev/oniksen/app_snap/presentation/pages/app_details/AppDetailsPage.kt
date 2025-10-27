package dev.oniksen.app_snap.presentation.pages.app_details

import android.content.res.Configuration
import android.media.VolumeShaper
import  androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalConfiguration
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppDetailsPageContent(
    modifier: Modifier = Modifier,
    appInfo: AppInfo,
    updateLastScanHash: (packageName: String, hash: String) -> Unit,
) {
    val isAppModified = appInfo.hashSum != appInfo.lastScanHash && appInfo.lastScanHash != null
    val localContext = LocalContext.current
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Левая часть: Header
            Header(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp),
                iconFilePath = appInfo.iconFilePath.toString(),
                appName = appInfo.appName,
                packageName = appInfo.packageName
            ) {
                val launchIntent = localContext.packageManager.getLaunchIntentForPackage(appInfo.packageName)
                if (launchIntent != null) {
                    localContext.startActivity(launchIntent)
                }
            }

            // Правая часть: прокручиваемая Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                if (isAppModified) {
                    ModifiedAppAlertField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                VersionField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    value = appInfo.appVersion ?: "Неизвестно",
                )

                HashSumField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    hashSum = appInfo.hashSum,
                )

                // Добавляем Spacer для заполнения пространства
                Spacer(modifier = Modifier.weight(1f))

                // Кнопка внизу, но без Box, чтобы не мешать прокрутке
                if (isAppModified) {
                    UpdateHashButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .wrapContentSize(Alignment.Center),
                        updateLastScanHash = {
                            updateLastScanHash(
                                appInfo.packageName,
                                appInfo.hashSum,
                            )
                        }
                    )
                }
            }
        }
    } else {
        // Portrait-ориентация: текущая реализация
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Header(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
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
                ModifiedAppAlertField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            VersionField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                value = appInfo.appVersion ?: "Неизвестно",
            )

            HashSumField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 20.dp),
                hashSum = appInfo.hashSum,
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.BottomCenter
            ) {
                if (isAppModified) {
                    UpdateHashButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .wrapContentSize(Alignment.Center),
                        updateLastScanHash = {
                            updateLastScanHash(
                                appInfo.packageName,
                                appInfo.hashSum,
                            )
                        }
                    )
                }
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
        modifier = modifier,
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
            modifier = Modifier
                .weight(1f)
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
            .padding(top = 8.dp, bottom = 20.dp),
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
private fun ModifiedAppAlertField(
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
                verticalAlignment = Alignment.CenterVertically,
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun AppDetailsPage_LandscapeLight() {
    MaterialExpressiveTheme {
        Surface {
            StateForPreview()
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun AppDetailsPage_LandscapeDark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            StateForPreview()
        }
    }
}