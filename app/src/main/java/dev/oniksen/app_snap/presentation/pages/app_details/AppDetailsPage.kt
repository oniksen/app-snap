package dev.oniksen.app_snap.presentation.pages.app_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.presentation.theme.displayFontFamily
import dev.oniksen.app_snap.utils.previewApps
import java.io.File

@Composable
fun AppDetailsPage(
    modifier: Modifier = Modifier,
    appInfo: AppInfo?,
) {
    if (appInfo != null) {
        val localContext = LocalContext.current

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically // КЛЮЧЕВОЕ: центрируем иконку по вертикали
            ) {
                Image(
                    modifier = Modifier
                        .weight(0.6f, fill = false) // 25% ширины, но не растягивается
                        .aspectRatio(1f),
                    painter = rememberAsyncImagePainter(File(appInfo.iconFilePath.toString())),
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
                            text = appInfo.appName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            fontFamily = displayFontFamily,
                        )
                        Text(
                            text = appInfo.packageName,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp)) // ФИКСИРОВАННЫЙ ОТСТУП

                    Button(
                        onClick = {
                            val launchIntent = localContext.packageManager.getLaunchIntentForPackage(appInfo.packageName)
                            if (launchIntent != null) {
                                localContext.startActivity(launchIntent)
                            }
                        },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text("Открыть")
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (appInfo.hashSum != appInfo.lastScanHash && appInfo.lastScanHash != null) {
                Card(
                    modifier = Modifier.padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text("Приложение модифицировано")
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                )
            ) {
                VersionSection(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    label = "Версия",
                    value = appInfo.appVersion ?: "Неизвестно"
                )
            }

            Card(
                modifier = Modifier
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
                        text = appInfo.hashSum,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("Приложение не найдено")
        }
    }
}

@Composable
private fun VersionSection(modifier: Modifier = Modifier, label: String, value: String) {
    FlowRow (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.outline,
        )
        Text(value)
    }
}

@Composable
private fun StateForPreview() {
    AppDetailsPage(appInfo = previewApps.first())
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