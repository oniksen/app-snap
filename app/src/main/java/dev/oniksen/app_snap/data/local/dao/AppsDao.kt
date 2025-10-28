package dev.oniksen.app_snap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.oniksen.app_snap.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AppsDao {
    @Query("SELECT * FROM AppInfo ORDER BY app_name COLLATE NOCASE")
    fun getApps(): Flow<List<AppInfo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<AppInfo>)

    @Query("SELECT COUNT(*) FROM AppInfo")
    suspend fun getCachedAppsCount(): Int

    @Transaction
    suspend fun upsertBatch(apps: List<AppInfo>) {
        insertAll(apps)
    }

    /**
     * Получить, сохранённую текущую, хэш-сумму приложения.
     * */
    @Query("SELECT hash_sum FROM AppInfo WHERE package_name = :pkg LIMIT 1")
    suspend fun fetchHashSumFor(pkg: String): String?

    /**
     * Получить, сохранённую с прошлого сканирования, хэш-сумму приложения.
     * */
    @Query("SELECT last_scan_hash FROM AppInfo WHERE package_name = :pkg LIMIT 1")
    suspend fun fetchLastScanHashFor(pkg: String): String?


    @Query("SELECT icon_file_path FROM AppInfo WHERE package_name = :pkg LIMIT 1")
    suspend fun getIconPathFor(pkg: String): String?

    @Query("UPDATE AppInfo SET last_scan_hash = :hash WHERE package_name = :pkg")
    suspend fun updateLastScanHash(pkg: String, hash: String)

    @Query("SELECT * FROM AppInfo WHERE package_name = :pkg")
    fun getAppByPackage(pkg: String): Flow<AppInfo?>
}
