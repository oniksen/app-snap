package dev.oniksen.app_snap.domain.model

import androidx.annotation.DrawableRes
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppInfo(
    @PrimaryKey val uuid: String,
    @ColumnInfo("package_name") val packageName: String,
    @ColumnInfo("app_name") val appName: String,
    @ColumnInfo("hash_sum") val hashSum: String,
    @param:DrawableRes @ColumnInfo("icon") val iconResId: Int,
)
