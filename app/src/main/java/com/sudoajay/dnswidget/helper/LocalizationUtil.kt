package com.sudoajay.dnswidget.helper

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import java.util.*


object LocalizationUtil {

    fun applyLanguage(context: Context, language: String): Context {
        val locale = Locale(language)
        val configuration = context.resources.configuration
        val displayMetrics = context.resources.displayMetrics

        Locale.setDefault(locale)

        configuration.locale = locale
        context.resources.updateConfiguration(configuration, displayMetrics)

        return context

    }


}
