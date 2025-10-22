package dev.oniksen.app_snap.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import dev.oniksen.app_snap.navigation.NavComponent
import dev.oniksen.app_snap.presentation.theme.AppSnapTheme
import dev.oniksen.app_snap.presentation.viewmodel.AppsViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val appsViewModel by viewModels<AppsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppSnapTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavComponent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = innerPadding.calculateTopPadding()),
                        appsViewModel = appsViewModel,
                    )
                }
            }
        }
    }
}