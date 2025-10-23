package dev.oniksen.app_snap.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import dev.oniksen.app_snap.data.local.AppsDataBase
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.domain.repository.AppsScanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest
import kotlin.math.roundToInt
import kotlin.uuid.ExperimentalUuidApi

class AppsScanRepositoryImpl(
    private val context: Context,
    private val db: AppsDataBase,
): AppsScanRepository {
    private val getAppsWithIconsIntent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    /**
     * Весь тяжелый код выполняется в Dispatchers.IO (в withContext).
     * Сначала собираем results в памяти, потом один insertAll — Room сработает один раз → одна эмиссия.
     * Мы сохраняем иконку только если checksum изменился или записи не было.
     * Используем packageName как стабильный PK.
     * */
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun scanApps(onProgress: (Int) -> Unit) = withContext(Dispatchers.IO) {
        val dao = db.appsDao()
        val pm = context.packageManager

        val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(getAppsWithIconsIntent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            pm.queryIntentActivities(getAppsWithIconsIntent, 0)
        }

        val result = mutableListOf<AppInfo>()
        var procesedApps = 0

        for (info in resolveInfo) {
            try {
                val appInfo = pm.getApplicationInfo(info.activityInfo.packageName, 0)
                val packageName = appInfo.packageName
                val version = pm.getPackageInfo(packageName, 0).versionName


                val appPaths = mutableListOf<String>()
                appInfo.sourceDir?.let { appPaths.add(it) } // Получаем базовый путь apk
                appInfo.splitSourceDirs?.let { splitDirs -> appPaths.addAll(splitDirs) }    // Получаем так же и дополнительные пути файлов приложения

                // вычисляем контрольную сумму по всем apk файлам приложения (конкатенируем)
                val combinedHash = computeCombinedSha256OfFiles(appPaths)

                // Если не удалось посчитать, то пропускаем.
                if (combinedHash == null) {
                    onProgress((++procesedApps / resolveInfo.size.toFloat() * 100).roundToInt())
                    continue
                }

                // Проверка кэша. Если hash не изменился, то ропускаем пересохранение иконки.
                val existingHash = dao.getHashSumFor(packageName)
                val needSaveIcon = existingHash == null || existingHash != combinedHash

                val iconPath = if (needSaveIcon) {
                    val icon = pm.getApplicationIcon(packageName)
                    saveAppIconToCache(
                        context = context,
                        packageName = packageName,
                        drawable = icon,
                    )
                } else {
                    // получить существующий путь из БД (можно получить через отдельный запрос или хранить map заранее)
                    // для упрощения, мы попытаемся прочитать из БД; если null, то пересохраним.
                    null
                }

                val appName = appInfo.loadLabel(pm).toString()
                result += AppInfo(
                    packageName = packageName,
                    appName = appName,
                    hashSum = combinedHash,
                    iconFilePath = iconPath ?: dao.getIconPathFor(packageName),
                    appVersion = version,
                    lastKnownHash = existingHash,
                )
            } catch (e: Exception) {
                Log.e(TAG, "fetchAppsInfo: Не удалось получить apk для ${info.activityInfo.packageName}", e)
            } finally {
                // Обновляем процент обработанных приложений.
                onProgress((++procesedApps / resolveInfo.size.toFloat() * 100).roundToInt())
            }

            // Вставляем батчем (единственной транзакцией). В таком случае Room заэмитит значение только 1 раз.
            dao.upsertBatch(result)
        }
    }

    override fun fetchAppsInfo(): Flow<List<AppInfo>> = db.appsDao().getApps().distinctUntilChanged()

    override suspend fun getCachedAppsCount(): Int {
        val dao = db.appsDao()
        return withContext(Dispatchers.IO) { dao.getCachedAppsCount() }
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun computeCombinedSha256OfFiles(paths: List<String>): String? {
        val md = MessageDigest.getInstance("SHA-256")
        var any = false
        val buffer = ByteArray(8 * 1024)
        for (p in paths) {
            val f = File(p)
            if (!f.exists()) continue
            any = true
            FileInputStream(f).use { fis ->
                var read = fis.read(buffer)
                while (read >= 0) {
                    md.update(buffer, 0, read)
                    read = fis.read(buffer)
                }
            }
        }
        return if (!any) null else md.digest().toHexString()
    }

    private fun saveAppIconToCache(context: Context, packageName: String, drawable: Drawable): String? {
        return try {
            // Преобразование Drawable в Bitmap
            val bitmap = drawable.toBitmap()
            // Создаём файл в кэше
            val file = File(context.filesDir, "icons/$packageName.png")
            file.parentFile?.mkdir()
            // Сохраняем Bitmap в файл
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val TAG = "apps_scan_repository_impl"
    }
}