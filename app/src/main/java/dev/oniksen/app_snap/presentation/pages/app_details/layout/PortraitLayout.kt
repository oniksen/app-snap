package dev.oniksen.app_snap.presentation.pages.app_details.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
fun PortraitLayout(
    params: AppDetailsLayoutParams,
) {
    Column(
        modifier = params.modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Header(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            iconFilePath = params.iconFilePath,
            appName = params.appName,
            packageName = params.packageName,
            openApp = params.openApp,
        )

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
                .padding(top = 8.dp, bottom = 20.dp),
            hashSum = params.hashSum,
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.BottomCenter
        ) {
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
    PortraitLayout(params = appDetailsParams)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(device = "id:pixel_5")
@Composable
private fun PortraitLayout_Light() {
    MaterialExpressiveTheme {
        Surface {
            StateForPreview()
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(device = "id:pixel_5")
@Composable
private fun PortraitLayout_Dark() {
    MaterialExpressiveTheme(darkColorScheme()) {
        Surface {
            StateForPreview()
        }
    }
}