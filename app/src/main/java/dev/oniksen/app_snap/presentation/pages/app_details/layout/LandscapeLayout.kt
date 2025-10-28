package dev.oniksen.app_snap.presentation.pages.app_details.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.oniksen.app_snap.presentation.pages.app_details.component.HashSumField
import dev.oniksen.app_snap.presentation.pages.app_details.component.Header
import dev.oniksen.app_snap.presentation.pages.app_details.component.ModifiedAppAlertField
import dev.oniksen.app_snap.presentation.pages.app_details.component.UpdateHashButton
import dev.oniksen.app_snap.presentation.pages.app_details.component.VersionField
import dev.oniksen.app_snap.presentation.pages.app_details.model.AppDetailsLayoutParams
import dev.oniksen.app_snap.utils.appDetailsParams

@Composable
fun LandscapeLayout(
    params: AppDetailsLayoutParams,
) {
    Row(
        modifier = params.modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Левая часть: Header
        Header(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp),
            iconFilePath = params.iconFilePath,
            appName = params.appName,
            packageName = params.packageName,
            openApp = params.openApp,
        )

        // Правая часть: прокручиваемая Column
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            if (params.isAppModified) {
                ModifiedAppAlertField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            VersionField(
                modifier = Modifier.fillMaxWidth(),
                value = params.version,
            )

            HashSumField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 20.dp),   // Добавляем отступ снизу для кнопки сохранения хэша.
                hashSum = params.hashSum,
            )

            // Добавляем Spacer для заполнения пространства
            Spacer(modifier = Modifier.weight(1f))

            // Кнопка внизу, но без Box, чтобы не мешать прокрутке
            if (params.isAppModified) {
                UpdateHashButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .wrapContentSize(Alignment.Center),
                    updateLastScanHash = params.updateLastScanHash,
                )
            }
        }
    }
}

@Composable
private fun StateForPreview() {
    LandscapeLayout(params = appDetailsParams,)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun LandscapeLayout_Light() {
    MaterialExpressiveTheme {
        Surface {
            StateForPreview()
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
private fun LandscapeLayout_Dark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            StateForPreview()
        }
    }
}