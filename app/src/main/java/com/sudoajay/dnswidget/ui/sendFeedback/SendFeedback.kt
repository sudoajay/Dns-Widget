package com.sudoajay.dnswidget.ui.sendFeedback

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.CustomToast


class SendFeedback : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setContentView(R.layout.activity_send_feeback)
        changeStatusBarColor()

        reference()

    }

    private fun reference(){
        val textView:TextView = findViewById(R.id.systemInfo_TextView)
        val text = getText(R.string.systemInfo_text)
        val sendFeedbackButton: Button = findViewById(R.id.sendFeedback_Button)
        val feedbackEditText :EditText = findViewById(R.id.feedback_EditText)

        val ss = SpannableString(text)

        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                CustomToast.toastIt(applicationContext,"Do Something")
                callCustomSystemInfo()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.BLUE
                ds.isUnderlineText = true
            }
        }


        ss.setSpan(clickableSpan1, 5, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()

        sendFeedbackButton.setOnClickListener {
            if(feedbackEditText.length() == 0) feedbackEditText.error= getString(R.string.feedbackEditTextError)
            else{
                CustomToast.toastIt(applicationContext,"We Send Here")
            }
        }


    }

    private fun callCustomSystemInfo() {
        val ft = supportFragmentManager.beginTransaction()
        val customDialogForBackgroundTimer = SystemInfo()
        customDialogForBackgroundTimer.show(ft, "dialog")
    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }


}
