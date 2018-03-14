package com.hunterdev.timerapp.utils

import android.content.Context
import android.preference.PreferenceManager
import com.hunterdev.timerapp.TimerActivity

class PrefUtil {
    companion object {

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.hunterdev.timer.previous_timer_length"
        private const val TIMER_LENGTH_REMAINING_SECONDS_ID = "com.hunterdev.timer.timer_length_remaining"
        private const val TIMER_STATE_ID = "com.hunterdev.timer.timer_state"
        private const val ALARM_SET_TIME_ID = "com.hunterdev.timer.background_time"
        private const val TIMER_LENGTH_ID = "com.hunterdev.timerapp.timer_length"

        fun getTimerLengthInMinutes(context: Context): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TIMER_LENGTH_ID, 10)
        }

        fun getPreviousTimerLengthInSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthInSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        fun getTimerState(context: Context): TimerActivity.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: TimerActivity.TimerState, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(TIMER_STATE_ID, state.ordinal)
            editor.apply()
        }

        fun getTimerLengthRemainingInSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(TIMER_LENGTH_REMAINING_SECONDS_ID, 0)
        }

        fun setTimerLengthRemainingInSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(TIMER_LENGTH_REMAINING_SECONDS_ID, seconds)
            editor.apply()
        }

        fun getAlarmSetTime(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(time: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID, time)
            editor.apply()
        }
    }
}
