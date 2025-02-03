package com.example.batterychargechecker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val appContext = app.applicationContext
    private val appSettings = AppSettings.getInstance(appContext)

    val monitorOn = appSettings.monitorOn.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun setMonitorOn(on: Boolean) {
        viewModelScope.launch {
            appSettings.updateMonitorOn(on)
        }
    }

    val monitorLevel = appSettings.monitorLevel.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun setMonitorLevel(level: Int) {
        viewModelScope.launch {
            appSettings.updateMonitorLevel(level)
        }
    }

    val notificationDuration =
        appSettings.notificationDuration.stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun setNotificationDuration(duration: Int) {
        viewModelScope.launch {
            appSettings.updateNotificationDuration(duration)
        }
    }

    val repeatCount = appSettings.repeatCount.stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun setRepeatCount(count: Int) {
        viewModelScope.launch {
            appSettings.updateRepeatCount(count)
        }
    }

    val repeatInterval =
        appSettings.repeatInterval.stateIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun setRepeatInterval(interval: Int) {
        viewModelScope.launch {
            appSettings.updateRepeatInterval(interval)
        }
    }
}