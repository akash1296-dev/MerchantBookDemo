package com.wedoapps.barcodescanner.Utils

import android.content.Context
import android.content.SharedPreferences
import java.util.prefs.Preferences

object PreferenceHelper {

    private var sharedPreferences: SharedPreferences? = null
    private const val PREFERENCE_NAME = "com.phonpebook"
    private const val IS_ONBOARDING_COMPLETED = "is_onboarding_completed"

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, 0)
    }

    fun isOnBoardingCompleted(): Boolean {
        return sharedPreferences?.getBoolean(IS_ONBOARDING_COMPLETED, false) ?: false
    }

    fun setOnBoardingCompleted() {
        sharedPreferences?.edit()?.putBoolean(IS_ONBOARDING_COMPLETED, true)?.commit()
    }
}