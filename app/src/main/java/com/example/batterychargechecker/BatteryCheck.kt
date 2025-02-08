package com.example.batterychargechecker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager.STREAM_ALARM
import android.media.AudioManager.STREAM_NOTIFICATION
import android.media.ToneGenerator
import android.os.BatteryManager
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private const val TAG = "BatteryCheck"

private data class BatteryStatus(val isCharging: Boolean, val level: Int) {
    fun isChargingFinished(targetLevel: Int): Boolean {
        return isCharging && targetLevel <= level
    }
}

suspend fun monitorBattery(context: Context, settingsData: AppSettingsData) {
    var done = false
    while (settingsData.monitorOn) {
        Log.d(TAG, "monitorBattery: app settings data = $settingsData")
        getBatteryStatus(context)?.let { s ->
            Log.d(TAG, "charging: ${s.isCharging}, level = ${s.level}, done = $done")
            if (s.isChargingFinished(settingsData.targetLevel)) {
                // 充電完了
                if (!done) {
                    done = notify(context, settingsData)
                }
            } else {
                // 未充電状態
                done = false
            }
        }
        delay(60 * 1000)
    }
}

private fun getBatteryStatus(context: Context): BatteryStatus? {
    val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { iFilter ->
        context.registerReceiver(null, iFilter)
    } ?: run {
        Log.e(TAG, "batteryStatus is null")
        return null
    }

    val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
    if (status == -1) {
        Log.e(TAG, "could not get EXTRA_STATUS ")
        return null
    }

    val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
            || status == BatteryManager.BATTERY_STATUS_FULL

    val batteryLevel = batteryStatus.let { intent ->
        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        if (level == -1) return null
        val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (scale == -1) return null
        (level * 100 / scale.toFloat()).roundToInt()
    }
    return BatteryStatus(isCharging = isCharging, level = batteryLevel)
}

private suspend fun notify(context: Context, settingsData: AppSettingsData): Boolean {
    repeat(settingsData.repeatCount) { i ->
        Log.d(TAG, "vibrate: $i")
        vibrate(context, settingsData.notificationDuration)
        Log.d(TAG, "exit vibrate: $i")
        if (settingsData.beepOn) beep(settingsData.beepStreamType)

        // 最後はdelayしない
        if (i != settingsData.repeatCount - 1) {
            delay(settingsData.repeatInterval * 60L * 1000)

            // バッテリーの状態を再度確認する
            getBatteryStatus(context)?.let { s ->
                if (!s.isChargingFinished(settingsData.targetLevel)) {
                    // アラーム中に充電完了状態を満たさなくなった
                    return false
                }
            }
        }
    }
    return true
}

private fun vibrate(context: Context, duration: Int) {
    val vibrator = getSystemService(context, Vibrator::class.java) ?: run {
        Log.e(TAG, "vibrator is null")
        return
    }
    if (26 <= Build.VERSION.SDK_INT) { // Android 8.0以上
        val timings = mutableListOf<Long>()
        repeat(duration * 2) {
            timings.add(500L)
        }
        val amplitudes = mutableListOf<Int>()
        repeat(duration) {
            amplitudes.add(DEFAULT_AMPLITUDE)
            amplitudes.add(0)
        }
        val vibrationEffect = VibrationEffect.createWaveform(
            timings.toLongArray(),
            amplitudes.toIntArray(),
            -1
        )
        vibrator.vibrate(vibrationEffect)
    } else {
        val timings = mutableListOf(0L)
        Log.d(TAG, "timings = $timings")
        repeat(duration * 2) {
            timings.add(500L)
        }
        vibrator.vibrate(timings.toLongArray(), -1)
    }
}

private fun beep(streamType: Int) {
    Log.d(TAG, "beep")
    val st = if (streamType == 0) STREAM_ALARM else STREAM_NOTIFICATION
    val gen = ToneGenerator(st, ToneGenerator.MAX_VOLUME)
    gen.startTone(ToneGenerator.TONE_CDMA_ABBR_REORDER)
}