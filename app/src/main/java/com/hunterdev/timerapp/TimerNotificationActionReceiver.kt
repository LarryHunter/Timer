package com.hunterdev.timerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hunterdev.timerapp.utils.AppConstants
import com.hunterdev.timerapp.utils.NotificationUtil
import com.hunterdev.timerapp.utils.PrefUtil
import java.util.concurrent.TimeUnit

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AppConstants.ACTION_STOP -> {
                TimerActivity.removeAlarm(context)
                PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
                NotificationUtil.hideTimerNotification(context)
            }
            AppConstants.ACTION_PAUSE -> {
                var secondsRemaining = PrefUtil.getTimerLengthRemainingInSeconds(context)
                val alarmSetTime = PrefUtil.getAlarmSetTime(context)
                val nowSeconds = TimerActivity.nowInSeconds

                TimerActivity.removeAlarm(context)
                secondsRemaining -= (nowSeconds - alarmSetTime)
                PrefUtil.setTimerLengthRemainingInSeconds(secondsRemaining, context)
                PrefUtil.setTimerState(TimerActivity.TimerState.Paused, context)
                NotificationUtil.showTimerPaused(context)
            }
            AppConstants.ACTION_RESUME -> {
                val secondsRemaining = PrefUtil.getTimerLengthRemainingInSeconds(context)
                setupStartVariables(context, secondsRemaining)
            }
            AppConstants.ACTION_START -> {
                val minutesRemaining = PrefUtil.getTimerLengthInMinutes(context)
                val secondsRemaining = TimeUnit.MINUTES.toSeconds(minutesRemaining.toLong())
                PrefUtil.setTimerLengthRemainingInSeconds(secondsRemaining, context)
                setupStartVariables(context, secondsRemaining)
            }
        }
    }

    private fun setupStartVariables(context: Context, secondsRemaining: Long) {
        val wakeUpTime = TimerActivity.setAlarm(context, TimerActivity.nowInSeconds, secondsRemaining)
        PrefUtil.setTimerState(TimerActivity.TimerState.Running, context)
        NotificationUtil.showTimerRunning(context, wakeUpTime)
    }
}
