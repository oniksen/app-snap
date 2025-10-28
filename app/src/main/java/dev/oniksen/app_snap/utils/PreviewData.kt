package dev.oniksen.app_snap.utils

import androidx.compose.ui.Modifier
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.presentation.pages.app_details.model.AppDetailsLayoutParams

val previewApps = listOf(
    AppInfo(
        packageName = "com.example.calendar",
        appName = "Календарь",
        hashSum = "abc123",
        iconFilePath = "",
        appVersion = "1.0.0",
        lastScanHash = "asdasd",
    ),
    AppInfo(
        packageName = "com.example.camera",
        appName = "Камера",
        hashSum = "def456",
         iconFilePath = "",
        appVersion = "1.0.0",
    ),
    AppInfo(
        packageName = "com.example.music",
        appName = "Музыка",
        hashSum = "ghi789",
         iconFilePath = "",
        appVersion = "1.0.0",
    ),
    AppInfo(
        packageName = "com.example.notes",
        appName = "Заметки",
        hashSum = "jkl012",
         iconFilePath = "",
        appVersion = "1.0.0",
    ),
    AppInfo(
        packageName = "com.example.browser",
        appName = "Браузер",
        hashSum = "mno345",
         iconFilePath = "",
        appVersion = "1.0.0",
    )
)

val appDetailsParams = AppDetailsLayoutParams(
    modifier = Modifier,
    isAppModified = true,
    iconFilePath = "/storage/emulated/0/Android/data/com.example.app/files/icon_modified.png",
    appName = "Telegram",
    packageName = "org.telegram.messenger",
    version = "10.5.0",
    hashSum = "xyz789uvw012",
    updateLastScanHash = { },
    openApp = { },
)