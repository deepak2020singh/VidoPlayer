package com.foss.vidoplay.data.repos

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.File

private const val TAG = "LastPlayedRepository"

data class LastPlayedInfo(
    val videoId: Long,
    val folderPath: String,
    val videoName: String,
    val videoPath: String,
    val thumbnailUri: String?,
    val duration: Long,
    val position: Long,
    val lastPlayedTimestamp: Long = System.currentTimeMillis()
)

class LastPlayedRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val KEY_VIDEO_ID = longPreferencesKey("last_video_id")
        private val KEY_FOLDER_PATH = stringPreferencesKey("last_folder_path")
        private val KEY_VIDEO_NAME = stringPreferencesKey("last_video_name")
        private val KEY_VIDEO_PATH = stringPreferencesKey("last_video_path")
        private val KEY_THUMBNAIL_URI = stringPreferencesKey("last_thumbnail_uri")
        private val KEY_DURATION = longPreferencesKey("last_duration")
        private val KEY_POSITION = longPreferencesKey("last_position")
        private val KEY_TIMESTAMP = longPreferencesKey("last_timestamp")

        const val MAX_RESUME_AGE_MS = 7 * 24 * 60 * 60 * 1000L
    }

    val lastPlayed: Flow<LastPlayedInfo?> = dataStore.data
        .catch { exception ->
            Log.e(TAG, "Error reading last played data", exception)
            emit(androidx.datastore.preferences.core.emptyPreferences())
        }
        .map { prefs ->
            try {
                val id = prefs[KEY_VIDEO_ID] ?: return@map null
                val path = prefs[KEY_FOLDER_PATH] ?: return@map null

                LastPlayedInfo(
                    videoId = id,
                    folderPath = path,
                    videoName = prefs[KEY_VIDEO_NAME] ?: "",
                    videoPath = prefs[KEY_VIDEO_PATH] ?: "",
                    thumbnailUri = prefs[KEY_THUMBNAIL_URI],
                    duration = prefs[KEY_DURATION] ?: 0L,
                    position = prefs[KEY_POSITION] ?: 0L,
                    lastPlayedTimestamp = prefs[KEY_TIMESTAMP] ?: 0L
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing last played data", e)
                null
            }
        }

    suspend fun save(info: LastPlayedInfo) {
        try {
            dataStore.edit { prefs ->
                prefs[KEY_VIDEO_ID] = info.videoId
                prefs[KEY_FOLDER_PATH] = info.folderPath
                prefs[KEY_VIDEO_NAME] = info.videoName
                prefs[KEY_VIDEO_PATH] = info.videoPath
                prefs[KEY_THUMBNAIL_URI] = info.thumbnailUri ?: ""
                prefs[KEY_DURATION] = info.duration
                prefs[KEY_POSITION] = info.position
                prefs[KEY_TIMESTAMP] = info.lastPlayedTimestamp
            }
            Log.d(TAG, "Saved last played info: ${info.videoName} at position ${info.position}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving last played info", e)
        }
    }

    suspend fun savePosition(position: Long) {
        try {
            dataStore.edit { prefs ->
                prefs[KEY_POSITION] = position
                prefs[KEY_TIMESTAMP] = System.currentTimeMillis()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving position", e)
        }
    }

    suspend fun clear() {
        try {
            dataStore.edit { prefs ->
                prefs.remove(KEY_VIDEO_ID)
                prefs.remove(KEY_FOLDER_PATH)
                prefs.remove(KEY_VIDEO_NAME)
                prefs.remove(KEY_VIDEO_PATH)
                prefs.remove(KEY_THUMBNAIL_URI)
                prefs.remove(KEY_DURATION)
                prefs.remove(KEY_POSITION)
                prefs.remove(KEY_TIMESTAMP)
            }
            Log.d(TAG, "Cleared last played data")
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing last played data", e)
        }
    }

    // Add this function for age-based cleanup
    suspend fun clearIfOlderThan(ageMs: Long) {
        try {
            // Collect current value
            var currentInfo: LastPlayedInfo? = null
            dataStore.data.collect { prefs ->
                val id = prefs[KEY_VIDEO_ID] ?: return@collect
                val path = prefs[KEY_FOLDER_PATH] ?: return@collect
                currentInfo = LastPlayedInfo(
                    videoId = id,
                    folderPath = path,
                    videoName = prefs[KEY_VIDEO_NAME] ?: "",
                    videoPath = prefs[KEY_VIDEO_PATH] ?: "",
                    thumbnailUri = prefs[KEY_THUMBNAIL_URI],
                    duration = prefs[KEY_DURATION] ?: 0L,
                    position = prefs[KEY_POSITION] ?: 0L,
                    lastPlayedTimestamp = prefs[KEY_TIMESTAMP] ?: 0L
                )
            }

            currentInfo?.let { info ->
                val age = System.currentTimeMillis() - info.lastPlayedTimestamp
                if (age > ageMs) {
                    clear()
                    Log.d(TAG, "Cleared old last played data (age: ${age / (24 * 60 * 60 * 1000)} days)")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing old entries", e)
        }
    }
}