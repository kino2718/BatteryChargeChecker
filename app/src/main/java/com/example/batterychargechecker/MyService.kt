package com.example.batterychargechecker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MyService : Service(), CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        createNotificationChannel()

        val appSettings = AppSettings.getInstance(applicationContext)

        launch {
            appSettings.monitorOn.collect { monitorOn ->
                Log.d(TAG, "monitorOn: $monitorOn")
                if (monitorOn) foregroundOn()
                else foregroundOff()
            }
        }

        launch {
            appSettings.appSettingsData
                .conflate()
                .onEach { delay(5 * 1000) } // sliderでの変更時、途中の値を適当に間引く
                .collectLatest { data ->
                    // 新しいdataが来たら以下の処理はcancelされる
                    Log.d(TAG, "appSettingsData = $data")
                    monitorBattery(applicationContext, data)
                    Log.d(TAG, "exit monitorBattery")
                }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        Log.d(TAG, "onDestroy")
    }

    // Binderを通してclientと通信はしない。単にActivityがForegroundの時にServiceが立ち上がっているようにしたいため。
    // startService,stopServiceを使わないのはServiceの停止はAppSettingsのmonitorOnがfalseの時のみ行いたいため。
    override fun onBind(p0: Intent?): IBinder {
        Log.d(TAG, "onBind")
        return object : Binder() {}
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    private var isForeground = false

    private fun foregroundOn() {
        Log.d(TAG, "foregroundOn")
        if (!isForeground) {
            ContextCompat.startForegroundService(
                this,
                Intent(this, MyService::class.java)
            )

            val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).build()
            val type = if (34 <= Build.VERSION.SDK_INT) FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0
            Log.d(TAG, "startForeground")
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                type
            )
            isForeground = true
        }
    }

    private fun foregroundOff() {
        Log.d(TAG, "foregroundOff")
        if (isForeground) {
            Log.d(TAG, "stopForeground")
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf()
            isForeground = false
        }
    }

    private fun createNotificationChannel() {
        if (26 <= Build.VERSION.SDK_INT) { // Android 8.0以上
            ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            )?.let { notificationManager ->
                val channelName = getString(R.string.channel_name)
                val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    companion object {
        private const val TAG = "MyService"
        private const val NOTIFICATION_CHANNEL_ID = "foreground_service"
        private const val NOTIFICATION_ID = 1

    }
}