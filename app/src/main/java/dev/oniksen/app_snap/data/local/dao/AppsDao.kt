package dev.oniksen.app_snap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.oniksen.app_snap.domain.model.AppInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface AppsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(app: AppInfo)

    @Query("SELECT * FROM AppInfo")
    fun getApps(): Flow<List<AppInfo>>

    @Query("SELECT COUNT(*) FROM AppInfo")
    fun getCachedAppsCount(): Int
}