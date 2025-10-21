package dev.oniksen.app_snap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.domain.repository.AppsScanRepository
import dev.oniksen.app_snap.presentation.viewmodel.contract.AppsViewModelContract
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val appsScanRepository: AppsScanRepository
): ViewModel(), AppsViewModelContract {
    private val _appsListIsRefreshing = MutableStateFlow(false)
    override val appsListIsRefreshing = _appsListIsRefreshing

    private val _appListState = appsScanRepository.fetchAppsInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )
    override val appListState = _appListState

    override fun rescanApps() {
        viewModelScope.launch {
            _appsListIsRefreshing.emit(true)

            appsScanRepository.scanApps { progress ->
                // TODO("Эмитить прогресс в ui flow")
            }

            _appsListIsRefreshing.emit(false)
        }
    }

    override fun scanIfNeed() {
        viewModelScope.launch {
            if (appsScanRepository.getCachedAppsCount() == 0) {
                rescanApps()
            }
        }
    }
}