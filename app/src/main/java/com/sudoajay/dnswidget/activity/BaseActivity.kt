package com.sudoajay.dnswidget.activity

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.LocalizationUtil
import com.sudoajay.dnswidget.ui.setting.SettingConfiguration
import java.util.*


open class BaseActivity : AppCompatActivity() {
    private lateinit var currentTheme: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTheme = getDarkMode(applicationContext)
        setAppTheme(currentTheme)

        val englishName =
            getLocaleStringResource(
                applicationContext,
                Locale("en"),
                R.string.menu_only_system_apps
            )
    }

    override fun onResume() {
        super.onResume()
        val theme = getDarkMode(applicationContext)
        if (currentTheme != theme)
            recreate()
    }
    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            getLocaleStringResource(applicationContext, Locale("en"), R.string.off_text) -> {
                setValue(false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            getLocaleStringResource(applicationContext, Locale("en"), R.string.automatic_at_sunset_text) -> setDarkMode(isSunset())
            getLocaleStringResource(applicationContext, Locale("en"), R.string.set_by_battery_saver_text) -> {
                setValue(isPowerSaveMode())
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                )
            }
            getLocaleStringResource(applicationContext, Locale("en"), R.string.system_default_text) -> {
                setValue(isSystemDefaultOn())
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
            else -> {
                setValue(true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

    }

    private fun setDarkMode(isDarkMode: Boolean) {
        setValue(isDarkMode)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    private fun isSunset(): Boolean {
        val rightNow: Calendar = Calendar.getInstance()
        val hour: Int = rightNow.get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour > 18
    }


    private fun setValue(isDarkMode: Boolean) {
        getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(getString(R.string.is_dark_mode_text), isDarkMode).apply()
    }


    private fun isSystemDefaultOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private fun isPowerSaveMode(): Boolean {
        val powerManager =
            getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isPowerSaveMode
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            LocalizationUtil.applyLanguage(
                newBase,
                SettingConfiguration.getLanguage(newBase)
            )
        )
    }

    companion object {

        fun getDarkMode(context: Context): String {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    getLocaleStringResource(context, Locale("en"), R.string.dark_mode_text),
                    getLocaleStringResource(context, Locale("en"), R.string.system_default_text)
                ).toString()
        }

        fun isDarkMode(context: Context): Boolean {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getBoolean(
                    getLocaleStringResource(context, Locale("en"), R.string.is_dark_mode_text), false
                )
        }

        fun getLocaleStringResource(
            context: Context,
            requestedLocale: Locale?,
            resourceId: Int
        ): String? {
            val result: String
            // use latest api
            val config =
                Configuration(context.resources.configuration)
            config.setLocale(requestedLocale)
            result = context.createConfigurationContext(config).getText(resourceId).toString()
            return result
        }
    }


}