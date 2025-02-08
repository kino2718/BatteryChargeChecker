package com.example.batterychargechecker

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

data class AppSettingsData(
    val monitorOn: Boolean,
    val targetLevel: Int,
    val notificationDuration: Int,
    val repeatCount: Int,
    val repeatInterval: Int,
    val beepOn: Boolean,
    val beepStreamType: Int,
)

class AppSettings private constructor(context: Context) {
    private val appContext = context.applicationContext

    val monitorOn = appContext.dataStore.data.map {
        it[PreferencesKeys.MONITOR_ON] ?: false
    }.distinctUntilChanged()

    val targetLevel = appContext.dataStore.data.map {
        it[PreferencesKeys.TARGET_LEVEL] ?: 80
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

    val beepOn = appContext.dataStore.data.map {
        it[PreferencesKeys.BEEP_ON] ?: false
    }

    val beepStreamType = appContext.dataStore.data.map {
        it[PreferencesKeys.BEEP_STREAM_TYPE] ?: 0
    }

    suspend fun updateMonitorOn(on: Boolean) {
        appContext.dataStore.edit {
            it[PreferencesKeys.MONITOR_ON] = on
        }
    }

    suspend fun updateTargetLevel(level: Int) {
        appContext.dataStore.edit {
            it[PreferencesKeys.TARGET_LEVEL] = level
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

    suspend fun updateBeepOn(on: Boolean) {
        appContext.dataStore.edit {
            it[PreferencesKeys.BEEP_ON] = on
        }
    }

    suspend fun updateBeepStreamType(type: Int) {
        appContext.dataStore.edit {
            it[PreferencesKeys.BEEP_STREAM_TYPE] = type
        }
    }

    private object PreferencesKeys {
        val MONITOR_ON = booleanPreferencesKey("monitor_on")
        val TARGET_LEVEL = intPreferencesKey("target_level")
        val NOTIFICATION_DURATION = intPreferencesKey("notification_duration")
        val REPEAT_COUNT = intPreferencesKey("repeat_count")
        val REPEAT_INTERVAL = intPreferencesKey("repeat_interval")
        val BEEP_ON = booleanPreferencesKey("beep_on")
        val BEEP_STREAM_TYPE = intPreferencesKey("beep_stream_type")
    }


    val appSettingsData = combine(
        monitorOn,
        targetLevel,
        notificationDuration,
        repeatCount,
        repeatInterval,
    ) { monitorOn, targetLevel, notificationDuration, repeatCount, repeatInterval ->
        AppSettingsData(
            monitorOn = monitorOn,
            targetLevel = targetLevel,
            notificationDuration = notificationDuration,
            repeatCount = repeatCount,
            repeatInterval = repeatInterval,
            beepOn = false,
            beepStreamType = 0,
        )
    }.combine(beepOn) { appSettingsData, beepOn ->
        appSettingsData.copy(beepOn = beepOn)
    }.combine(beepStreamType) { appSettingsData, beepStreamType ->
        appSettingsData.copy(beepStreamType = beepStreamType)
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
