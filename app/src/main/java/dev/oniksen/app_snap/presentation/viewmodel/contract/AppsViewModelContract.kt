package dev.oniksen.app_snap.presentation.viewmodel.contract

import dev.oniksen.app_snap.domain.model.AppInfo
import kotlinx.coroutines.flow.StateFlow

interface AppsViewModelContract {

    val appListState: StateFlow<List<AppInfo>>

    fun scanApps()
}