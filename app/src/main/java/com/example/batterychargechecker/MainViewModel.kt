package com.example.batterychargechecker

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
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

    val targetLevel = appSettings.targetLevel.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun setTargetLevel(level: Int) {
        viewModelScope.launch {
            appSettings.updateTargetLevel(level)
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

    val beepOn =
        appSettings.beepOn.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun setBeepOn(b: Boolean) {
        viewModelScope.launch {
            appSettings.updateBeepOn(b)
        }
    }

    val beepStreamType =
        appSettings.beepStreamType.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun setBeepStreamType(type: Int) {
        viewModelScope.launch {
            appSettings.updateBeepStreamType(type)
        }
    }

    fun bindMyService() {
        Log.d(TAG, "bindMyService")
        val i = Intent(appContext, MyService::class.java)
        appContext.bindService(i, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindMyService() {
        Log.d(TAG, "unbindMyService")
        appContext.unbindService(serviceConnection)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "ServiceConnection.onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "ServiceConnection.onServiceDisconnected")
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}