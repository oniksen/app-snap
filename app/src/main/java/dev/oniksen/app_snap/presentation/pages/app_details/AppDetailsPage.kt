package dev.oniksen.app_snap.presentation.pages.app_details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.presentation.pages.app_details.layout.LandscapeLayout
import dev.oniksen.app_snap.presentation.pages.app_details.layout.PortraitLayout
import dev.oniksen.app_snap.presentation.pages.app_details.model.AppDetailsLayoutParams
import dev.oniksen.app_snap.presentation.viewmodel.contract.AppsViewModelContract

@Composable
fun AppDetailsPage(
    modifier: Modifier = Modifier,
    packageName: String,
    appsViewModel: AppsViewModelContract,
    updateLastScanHash: (packageName: String, hash: String) -> Unit,
) {
    val appInfo by appsViewModel.fetchAppInfo(packageName).collectAsStateWithLifecycle(null)

    if (appInfo != null) {
        AppDetailsLayoutSelector(
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
private fun AppDetailsLayoutSelector(
    modifier: Modifier = Modifier,
    appInfo: AppInfo,
    updateLastScanHash: (packageName: String, hash: String) -> Unit,
) {
    val isAppModified = appInfo.hashSum != appInfo.lastScanHash && appInfo.lastScanHash != null
    val localContext = LocalContext.current
    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val params = AppDetailsLayoutParams(
        modifier = modifier,
        isAppModified = isAppModified,
        iconFilePath = appInfo.iconFilePath.toString(),
        appName = appInfo.appName,
        packageName = appInfo.packageName,
        version = appInfo.appVersion ?: "Неизвестно",
        hashSum = appInfo.hashSum,
        updateLastScanHash = {
            updateLastScanHash(
                appInfo.packageName,
                appInfo.hashSum,
            )
        },
        openApp = {
            val launchIntent =
                localContext.packageManager.getLaunchIntentForPackage(appInfo.packageName)
            if (launchIntent != null) {
                localContext.startActivity(launchIntent)
            }
        }
    )

    if (isLandscape) {
        LandscapeLayout(params)
    } else {
        PortraitLayout(params)
    }
}