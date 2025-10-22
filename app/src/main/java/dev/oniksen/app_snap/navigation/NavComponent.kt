package dev.oniksen.app_snap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.presentation.pages.app_details.AppDetailsPage
import dev.oniksen.app_snap.presentation.pages.app_list.AppListPage

@Composable
fun NavComponent(
    modifier: Modifier,
    apps: List<AppInfo>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destination.AppList
    ) {
        composable<Destination.AppList> {
            AppListPage(
                modifier = modifier,
                apps = apps,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
                onItemClick = { appInfo ->
                    navController.navigate(Destination.AppDetails(appInfo.packageName))
                }
            )
        }
        composable<Destination.AppDetails> { backStackEntry ->
            val args: Destination.AppDetails = backStackEntry.toRoute()

            val appInfo = apps.find { it.packageName == args.packageName }

            AppDetailsPage (
                modifier = modifier,
                appInfo = appInfo,
            )
        }
    }
}