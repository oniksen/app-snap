package dev.oniksen.app_snap.domain.repository

import dev.oniksen.app_snap.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

interface AppsScanRepository {

    /**
     * Получение данных по всем установленным приложениям.
     * */
    suspend fun scanApps(onProgress: (Float) -> Unit)

    /**
     * Получить список кэшированных приложений.
     * */
    fun fetchAppsInfo(): Flow<List<AppInfo>>

    /**
     * Напрямую получить список кэшированных приложений.
     * */
    suspend fun getCachedAppsCount(): Int
}