package dev.oniksen.app_snap.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dev.oniksen.app_snap.domain.model.AppInfo
import dev.oniksen.app_snap.presentation.pages.app_details.AppDetailsPage
import dev.oniksen.app_snap.presentation.pages.app_list.AppListPage
import dev.oniksen.app_snap.presentation.viewmodel.contract.AppsViewModelContract

@Composable
fun NavComponent(
    modifier: Modifier,
    appsViewModel: AppsViewModelContract,
    navController: NavHostController,
) {

    NavHost(
        navController = navController,
        startDestination = Destination.AppList
    ) {
        composable<Destination.AppList> {
            AppListPage(
                modifier = modifier,
                appsViewModel = appsViewModel
            ) { clickedApp ->
                navController.navigate(Destination.AppDetails(clickedApp.packageName))
            }
        }

        composable<Destination.AppDetails> { backStackEntry ->
            val args: Destination.AppDetails = backStackEntry.toRoute()
            val appInfo = appsViewModel.getAppInfo(args.packageName)

            AppDetailsPage (
                modifier = modifier,
                appInfo = appInfo,
            )
        }
    }
}