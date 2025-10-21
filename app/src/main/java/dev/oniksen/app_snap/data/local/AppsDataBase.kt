package dev.oniksen.app_snap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.oniksen.app_snap.data.local.dao.AppsDao
import dev.oniksen.app_snap.domain.model.AppInfo

@Database(
    entities = [AppInfo::class],
    version = 4,
)
abstract class AppsDataBase: RoomDatabase() {
    abstract fun appsDao(): AppsDao
}