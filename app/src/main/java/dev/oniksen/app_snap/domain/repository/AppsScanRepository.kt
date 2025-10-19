package dev.oniksen.app_snap.domain.repository

interface AppsScanRepository {

    /**
     * Получение данных по всем установленным приложениям.
     * */
    suspend fun fetchAppsInfo(onProgress: (Int) -> Unit)
}