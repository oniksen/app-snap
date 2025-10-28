package dev.oniksen.app_snap.navigation

import kotlinx.serialization.Serializable

sealed class Destination {
    @Serializable
    object AppList : Destination()

    @Serializable
    data class AppDetails(val packageName: String) : Destination()
}