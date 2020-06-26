package com.sudoajay.dnswidget.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sudoajay.dnswidget.R

open class BaseActivity : AppCompatActivity() {
    private lateinit var currentTheme: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         currentTheme = getDarkMode(applicationContext)
        setAppTheme(currentTheme)
    }

    override fun onResume() {
        super.onResume()
        val theme = getDarkMode(applicationContext)
        if (currentTheme != theme)
            recreate()
    }

    private fun setAppTheme(currentTheme: String) {
        when (currentTheme) {
            getString(R.string.off_text) -> setTheme(
                R.style.AppTheme
            )
            else -> setTheme(R.style.DarkTheme)
        }
    }

    companion object {
         fun getDarkMode(context: Context): String {
            return context.getSharedPreferences("state", Context.MODE_PRIVATE)
                .getString(
                    context.getString(R.string.dark_mode_text), context.getString(
                        R.string.off_text
                    )
                ).toString()
        }
    }
}