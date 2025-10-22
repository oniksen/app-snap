package dev.oniksen.app_snap.presentation.pages.app_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.utils.previewApps

@Composable
fun AppDetailsPage(
    modifier: Modifier = Modifier,
    appInfo: AppInfo?,
) {
    if (appInfo != null) {
        val localContext = LocalContext.current

        Column(
            modifier = modifier.fillMaxSize()
        ) {
            LineInfo("Название приложения:", appInfo.appName)
            LineInfo("Версия:", appInfo.appVersion ?: "Неизвестно")
            LineInfo("Имя пакета:", appInfo.packageName)
            LineInfo("Контрольная сумма (sha-256):", appInfo.hashSum)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    val launchIntent = localContext.packageManager.getLaunchIntentForPackage(appInfo.packageName)
                    if (launchIntent != null) {
                        localContext.startActivity(launchIntent)
                    }
                }
            ) {
                Text("Открыть приложение")
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
private fun LineInfo(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label)
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