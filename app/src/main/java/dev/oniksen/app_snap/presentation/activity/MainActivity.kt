package dev.oniksen.app_snap.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.rememberAsyncImagePainter
import dagger.hilt.android.AndroidEntryPoint
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.navigation.Destination
import dev.oniksen.app_snap.navigation.NavComponent
import dev.oniksen.app_snap.presentation.theme.AppSnapTheme
import dev.oniksen.app_snap.presentation.viewmodel.AppsViewModel
import java.io.File

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val appsViewModel by viewModels<AppsViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppSnapTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                var title by remember { mutableStateOf("Список приложений") }
                var isNavigationIconVisible by remember { mutableStateOf(false) }

                LaunchedEffect(currentBackStack) {
                    currentBackStack?.destination?.route?.let { currentDestination ->
                        isNavigationIconVisible = currentDestination != Destination.AppList::class.qualifiedName
                        title = if (currentDestination == Destination.AppList::class.qualifiedName) {
                            "Список приложений"
                        } else {
                            "Информация"
                        }
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(text = title)
                            },
                            navigationIcon = {
                                if(isNavigationIconVisible) {
                                    IconButton(
                                        onClick = {
                                            navController.navigateUp()
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowBackIosNew,
                                            contentDescription = null,
                                        )
                                    }
                                }
                            },
                        )
                    },
                ) { innerPadding ->
                    NavComponent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        appsViewModel = appsViewModel,
                        navController = navController,
                    )
                }
            }
        }
    }
}