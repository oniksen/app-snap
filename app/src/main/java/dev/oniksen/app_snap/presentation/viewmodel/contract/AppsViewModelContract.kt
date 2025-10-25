package dev.oniksen.app_snap.presentation.viewmodel.contract

import dev.oniksen.app_snap.domain.model.AppInfo
import kotlinx.coroutines.flow.StateFlow

interface AppsViewModelContract {
    val appsListIsRefreshing: StateFlow<Pair<Boolean, Float>>

    val appListState: StateFlow<List<AppInfo>>

    fun rescanApps()

    fun scanIfNeed()

    fun getAppInfo(packageName: String): AppInfo?
}