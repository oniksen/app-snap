package dev.oniksen.app_snap.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import dev.oniksen.app_snap.data.local.AppsDataBase
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.domain.repository.AppsScanRepository
import dev.oniksen.app_snap.presentation.activity.byteArrayToHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AppsScanRepositoryImpl(
    private val context: Context,
    private val db: AppsDataBase,
): AppsScanRepository {
    private val getAppsWithIconsIntent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun scanApps(
        onProgress: (Int) -> Unit
    ) {
        val dao = db.appsDao()
        val pm = context.packageManager

        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(getAppsWithIconsIntent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            pm.queryIntentActivities(getAppsWithIconsIntent, 0)
        }

        var procesedApps = 0

        for (info in resolveInfo) {
            try {
                val appPaths = mutableListOf<String>()

                val appInfo = pm.getApplicationInfo(info.activityInfo.packageName, 0)

                // Получаем базовый путь apk
                appInfo.sourceDir?.let { appPaths.add(it) }
                // Получаем так же и дополнительные пути файлов приложения
                appInfo.splitSourceDirs?.let { splitDirs ->
                    appPaths.addAll(splitDirs)
                }

                for (path in appPaths) {
                    File(path).let { file ->
                        if (file.exists()) {
                            val checksum = file.calculateSha256()
                            Log.d(TAG, "fetchAppsInfo: checksum: $checksum")

                            // Кэширование полученной суммы.
                            withContext(Dispatchers.IO) {
                                dao.insert(
                                    AppInfo(
                                        uuid = Uuid.random().toHexString(),
                                        packageName = appInfo.packageName,
                                        appName = appInfo.loadLabel(pm).toString(),
                                        hashSum = checksum,
                                        iconResId = appInfo.icon,
                                    )
                                )
                            }
                        } else {
                            // TODO("Действие при отсутствии файла")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "fetchAppsInfo: Не удалось получить apk для ${info.activityInfo.packageName}", e)
            }

            // Обновляем процент обработанных приложений.
            onProgress(
                (++procesedApps / resolveInfo.size.toFloat() * 100).roundToInt()
            )
        }
    }

    override fun fetchAppsInfo(): Flow<List<AppInfo>> = db.appsDao().getApps()

    private fun File.calculateSha256(): String {
        val md = MessageDigest.getInstance("SHA-256")

        FileInputStream(this).use { fis ->
            DigestInputStream(fis, md).use { dis ->
                val buffer = ByteArray(8 * 1024)
                while (dis.read(buffer) != -1) { /* просто читаем чтобы обновить digest */ }
            }
        }

        return byteArrayToHex(md.digest())
    }

    companion object {
        const val TAG = "apps_scan_repository_impl"
    }
}