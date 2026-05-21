package com.example.nextstep.data.local
//mudar p DataStore dps, por quanto fica assim
import android.content.Context

class AppPreferences(
    private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        "nextstep_preferences",
        Context.MODE_PRIVATE
    )

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean("is_first_launch", true)
    }

    fun setFirstLaunchCompleted() {
        sharedPreferences.edit()
            .putBoolean("is_first_launch", false)
            .apply()
    }
}