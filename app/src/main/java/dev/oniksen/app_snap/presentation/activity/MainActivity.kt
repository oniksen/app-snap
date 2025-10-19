package dev.oniksen.app_snap.presentation.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.oniksen.app_snap.presentation.theme.AppSnapTheme
import dev.oniksen.app_snap.presentation.theme.bodyFontFamily
import java.io.File
import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import kotlin.experimental.and

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        searchAllAppsAndApkChecksum(context = applicationContext)

        setContent {
            AppSnapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Test on english и русскомьъ",
            modifier = modifier,
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = bodyFontFamily,
        )
    }
}

fun byteArrayToHex(bytes: ByteArray): String {
    val sb = StringBuilder(bytes.size * 2)
    for (b in bytes) {
        sb.append(String.format("%02x", b and 0xff.toByte()))
    }
    return sb.toString()
}

fun sha256OfFile(file: File): String {
    val md = MessageDigest.getInstance("SHA-256")
    FileInputStream(file).use { fis ->
        DigestInputStream(fis, md).use { dis ->
            val buffer = ByteArray(8 * 1024)
            while (dis.read(buffer) != -1) { /* просто читаем чтобы обновить digest */ }
        }
    }
    return byteArrayToHex(md.digest())
}

// usage внутри вашей функции:
private fun searchAllAppsAndApkChecksum(context: Context) {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val resolvedInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0L))
    } else {
        @Suppress("DEPRECATION")
        pm.queryIntentActivities(mainIntent, 0)
    }

    for (info in resolvedInfo) {
        val pkg = info.activityInfo.packageName
        Log.d("search", "package: $pkg")

        try {
            val appInfo = pm.getApplicationInfo(pkg, 0)
            val apkPaths = mutableListOf<String>()
            appInfo.sourceDir?.let { apkPaths.add(it) } // base APK
            val splitDirs = appInfo.splitSourceDirs
            if (splitDirs != null) {
                apkPaths.addAll(splitDirs)
            }

            for (path in apkPaths) {
                val file = File(path)
                if (file.exists()) {
                    val sha256 = sha256OfFile(file)
                    Log.d("search", "APK path=$path sha256=$sha256")
                } else {
                    Log.w("search", "APK not found at $path")
                }
            }
        } catch (e: Exception) {
            Log.e("search", "Error getting APK for $pkg: ${e.message}", e)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppSnapTheme {
        Greeting("Android")
    }
}