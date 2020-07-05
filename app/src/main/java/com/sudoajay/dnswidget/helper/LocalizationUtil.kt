package com.sudoajay.dnswidget.helper

import android.content.Context
import java.util.*


object LocalizationUtil {

    fun Context.changeLocale(language:String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = this.resources.configuration
        config.setLocale(locale)
        return createConfigurationContext(config)
    }


}
