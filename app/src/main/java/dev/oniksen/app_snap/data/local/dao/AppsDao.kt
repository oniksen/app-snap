package dev.oniksen.app_snap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import dev.oniksen.app_snap.domain.model.AppInfo

@Dao
interface AppsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(app: AppInfo)
}