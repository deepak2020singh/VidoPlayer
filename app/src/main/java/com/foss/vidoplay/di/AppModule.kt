package com.foss.vidoplay.di

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import androidx.room.Room
import com.foss.vidoplay.data.local.db.AppDatabase
import com.foss.vidoplay.data.repos.LastPlayedRepository
import com.foss.vidoplay.data.repos.PlaylistRepositoryImpl
import com.foss.vidoplay.data.repos.ThemePreferencesRepository
import com.foss.vidoplay.data.repos.VideoRepositoryImpl
import com.foss.vidoplay.domain.repo.PlaylistRepository
import com.foss.vidoplay.domain.repo.VideoRepository
import com.foss.vidoplay.presentation.viewModel.ExoPlayerViewModel
import com.foss.vidoplay.presentation.viewModel.GetVideosUseCase
import com.foss.vidoplay.presentation.viewModel.LastPlayedViewModel
import com.foss.vidoplay.presentation.viewModel.PlaylistViewModel
import com.foss.vidoplay.presentation.viewModel.ThemeViewModel
import com.foss.vidoplay.presentation.viewModel.VideoViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


private const val USER_PREFERENCES = "user_pre"


val appModule = module {
    single {
        PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(produceNewData = { emptyPreferences() }),
            migrations = listOf(SharedPreferencesMigration(get(), USER_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO), // Changed to IO for file ops
            produceFile = { get<Context>().preferencesDataStoreFile(USER_PREFERENCES) })
    }

    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "vidoplay.db")
            .fallbackToDestructiveMigration(false).build()
    }


    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().playlistVideoDao() }

    single<VideoRepository> { VideoRepositoryImpl(androidContext()) }
    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get()) }

    factory { GetVideosUseCase(get()) }

    viewModel { ExoPlayerViewModel(get()) }
    viewModel { VideoViewModel(get()) }
    viewModel { PlaylistViewModel(get()) }
    single { LastPlayedRepository(get()) }
    viewModel { LastPlayedViewModel(get()) }
    single { ThemePreferencesRepository(get()) }
    viewModel { ThemeViewModel(get()) }
}