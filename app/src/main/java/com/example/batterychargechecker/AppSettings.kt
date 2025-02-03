package com.example.batterychargechecker

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class AppSettings private constructor(context: Context) {
    private val appContext = context.applicationContext

    val monitorOn = appContext.dataStore.data.map {
        it[PreferencesKeys.MONITOR_ON] ?: false
    }.distinctUntilChanged()

    val monitorLevel = appContext.dataStore.data.map {
        it[PreferencesKeys.MONITOR_LEVEL] ?: 80
    }.distinctUntilChanged()

    val notificationDuration = appContext.dataStore.data.map {
        it[PreferencesKeys.NOTIFICATION_DURATION] ?: 5
    }.distinctUntilChanged()

    val repeatCount = appContext.dataStore.data.map {
        it[PreferencesKeys.REPEAT_COUNT] ?: 3
    }.distinctUntilChanged()

    val repeatInterval = appContext.dataStore.data.map {
        it[PreferencesKeys.REPEAT_INTERVAL] ?: 1
    }

    suspend fun updateMonitorOn(on: Boolean) {
        appContext.dataStore.edit {
            it[PreferencesKeys.MONITOR_ON] = on
        }
    }

    suspend fun updateMonitorLevel(level: Int) {
        appContext.dataStore.edit {
            it[PreferencesKeys.MONITOR_LEVEL] = level
        }
    }

    suspend fun updateNotificationDuration(duration: Int) {
        appContext.dataStore.edit {
            it[PreferencesKeys.NOTIFICATION_DURATION] = duration
        }
    }

    suspend fun updateRepeatCount(count: Int) {
        appContext.dataStore.edit {
            it[PreferencesKeys.REPEAT_COUNT] = count
        }
    }

    suspend fun updateRepeatInterval(interval: Int) {
        appContext.dataStore.edit {
            it[PreferencesKeys.REPEAT_INTERVAL] = interval
        }
    }

    private object PreferencesKeys {
        val MONITOR_ON = booleanPreferencesKey("monitor_on")
        val MONITOR_LEVEL = intPreferencesKey("monitor_level")
        val NOTIFICATION_DURATION = intPreferencesKey("notification_duration")
        val REPEAT_COUNT = intPreferencesKey("repeat_count")
        val REPEAT_INTERVAL = intPreferencesKey("repeat_interval")
    }

    companion object {
        @Volatile
        private var instance: AppSettings? = null

        fun getInstance(
            context: Context
        ): AppSettings {
            synchronized(this) {
                if (instance != null) return instance!!
                return AppSettings(context).also { instance = it }
            }
        }
    }
}