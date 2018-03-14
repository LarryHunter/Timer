package com.hunterdev.timerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hunterdev.timerapp.utils.NotificationUtil
import com.hunterdev.timerapp.utils.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)
        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }
}
