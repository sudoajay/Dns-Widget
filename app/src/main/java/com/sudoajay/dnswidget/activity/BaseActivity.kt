package com.sudoajay.dnswidget.activity

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.sudoajay.dnswidget.R
import java.util.*


open class BaseActivity : AppCompatActivity() {
    private lateinit var currentTheme: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTheme = getDarkMode(applicationContext)
        setAppTheme(currentTheme)
        Log.i("DarkMode", isSystemDefaultOn().toString())

    }

    override fun onResume() {
        super.onResume()
        val theme = getDarkMode(applicationContext)
        if (currentTheme != theme)
            recreate()
    }

    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            getString(R.string.off_text) -> saveTheme(false)
            getString(R.string.automatic_at_sunset_text) -> saveTheme(isSunset())
            getString(R.string.set_by_battery_saver_text) -> saveTheme(isPowerSaveMode())
            getString(R.string.system_default_text) -> saveTheme(isSystemDefaultOn())
            else -> saveTheme(true)
        }
    }

    private fun saveTheme(isDarkMode: Boolean) {
        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(getString(R.string.is_dark_mode_text), isDarkMode).apply()
        if (isDarkMode) {
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
    }


    private fun isSystemDefaultOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    private fun isPowerSaveMode(): Boolean {
        val powerManager =
            getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode
    }

    private fun isSunset(): Boolean {
        val rightNow: Calendar = Calendar.getInstance()
        val hour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour > 18
    }


    companion object {

        fun getDarkMode(context: Context): String {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.dark_mode_text), context.getString(
                        R.string.system_default_text
                    )
                ).toString()
        }

        fun isDarkMode(context: Context): Boolean {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getBoolean(
                    context.getString(R.string.is_dark_mode_text), false
                )

        }
    }


}