package com.example.batterychargechecker

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class MyService : Service() {
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
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

    companion object {
        private const val TAG = "MyService"
    }
}