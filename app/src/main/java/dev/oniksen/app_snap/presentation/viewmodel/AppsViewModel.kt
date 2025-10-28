package dev.oniksen.app_snap.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.domain.repository.AppsScanRepository
import dev.oniksen.app_snap.presentation.viewmodel.contract.AppsViewModelContract
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(
    private val appsScanRepository: AppsScanRepository
): ViewModel(), AppsViewModelContract {
    private val _appsListIsRefreshing = MutableStateFlow(Pair(false, 0f))
    @OptIn(FlowPreview::class)
    override val appsListIsRefreshing: StateFlow<Pair<Boolean, Float>> = _appsListIsRefreshing
        .debounce(100) // Обновлять не чаще, чем раз в 100 мс
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(false, 0f))

    private val _appListState = appsScanRepository.fetchAppsInfo().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )
    override val appListState = _appListState

    override fun rescanApps() {
        viewModelScope.launch {
            _appsListIsRefreshing.emit(Pair(true, 0f))

            appsScanRepository.scanApps { progress ->
                Log.d(TAG, "rescanApps: progress = $progress")
                _appsListIsRefreshing.tryEmit(Pair(true, progress))
            }

            _appsListIsRefreshing.emit(Pair(false, 0f))
        }
    }

    override fun scanIfNeed() {
        viewModelScope.launch {
            if (appsScanRepository.getCachedAppsCount() == 0) {
                rescanApps()
            }
        }
    }

    override fun getAppInfo(packageName: String) = _appListState.value.find { it.packageName == packageName }

    override fun fetchAppInfo(packageName: String): Flow<AppInfo?> = appsScanRepository.fetchAppByPackage(packageName)

    override fun updateLastScanHash(packageName: String, hash: String) {
        viewModelScope.launch {
            appsScanRepository.updateLastScanHash(packageName, hash)
        }
    }

    private companion object {
        const val TAG = "AppsViewModel"
    }
}