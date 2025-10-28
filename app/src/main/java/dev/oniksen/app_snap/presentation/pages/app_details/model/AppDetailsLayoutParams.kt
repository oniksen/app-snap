package dev.oniksen.app_snap.presentation.pages.app_details.model

import androidx.compose.ui.Modifier

data class AppDetailsLayoutParams(
    val modifier: Modifier,
    val isAppModified: Boolean,
    val iconFilePath: String,
    val appName: String,
    val packageName: String,
    val version: String,
    val hashSum: String,
    val updateLastScanHash: () -> Unit,
    val openApp: () -> Unit,
)
