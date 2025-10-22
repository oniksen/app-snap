package dev.oniksen.app_snap.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.oniksen.app_snap.data.local.AppsDataBase
import dev.oniksen.app_snap.data.repository.AppsScanRepositoryImpl
import dev.oniksen.app_snap.domain.repository.AppsScanRepository

@Module
@InstallIn(SingletonComponent::class)
class MainModule {

    @Provides
    fun provideScanRepo(
        @ApplicationContext context: Context
    ): AppsScanRepository {

        val db = Room.databaseBuilder(
            context = context,
            klass = AppsDataBase::class.java,
            name = "apps_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()

        return AppsScanRepositoryImpl(
            context = context,
            db = db,
        )
    }
}