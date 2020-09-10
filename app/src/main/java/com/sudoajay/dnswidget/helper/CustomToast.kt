package com.sudoajay.dnswidget.helper

import android.content.Context
import android.graphics.PorterDuff
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.sudoajay.dnswidget.R

object CustomToast {
    fun toastIt(mContext: Context, mes: String) {
        val toast = Toast.makeText(mContext, mes, Toast.LENGTH_LONG)
        toast.show()
    }
}