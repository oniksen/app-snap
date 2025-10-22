package dev.oniksen.app_snap.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class AppInfo(
    @PrimaryKey @ColumnInfo("package_name") val packageName: String,
    @ColumnInfo("app_name") val appName: String,
    @ColumnInfo("app_version") val appVersion: String?,
    @ColumnInfo("hash_sum") val hashSum: String,
    @ColumnInfo("icon_file_path") val iconFilePath: String?,
)
