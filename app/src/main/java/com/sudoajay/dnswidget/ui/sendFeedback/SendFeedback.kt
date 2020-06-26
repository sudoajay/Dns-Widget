package com.sudoajay.dnswidget.ui.sendFeedback

import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.sudoajay.dnswidget.activity.BaseActivity
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.CustomToast


class SendFeedback : BaseActivity(){
    private val requestCode = 100
    private var imageUri: Uri? = null
    private lateinit var feedbackEditText: EditText
    private lateinit var addScreenshotTextView: TextView
    private lateinit var addScreenshotSmallImageView: ImageView
    private lateinit var addScreenshotLargeImageView: ImageView



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
        feedbackEditText = findViewById(R.id.feedback_EditText)
        addScreenshotTextView = findViewById(R.id.addScreenshot_TextView)
        addScreenshotSmallImageView = findViewById(R.id.addScreenshotSmall_ImageView)
        addScreenshotLargeImageView = findViewById(R.id.addScreenshotLarge_ImageView)

        val imageButton: Button = findViewById(R.id.image_Button)

        val ss = SpannableString(text)

        val clickableSpan1: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                callCustomSystemInfo()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(applicationContext, R.color.textBlackColor)
                ds.isUnderlineText = true
                ds.bgColor = Color.WHITE
            }
        }


        ss.setSpan(clickableSpan1, 5, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()

        sendFeedbackButton.setOnClickListener {
            if (feedbackEditText.length() == 0) {
                CustomToast.toastIt(applicationContext, getString(R.string.feedbackEditTextError))
                feedbackEditText.error = getString(R.string.feedbackEditTextError)
            } else {

                openEmail()
            }
        }

        imageButton.setOnClickListener {
            openImageManager()
        }

    }

    private fun openImageManager() {
        val intent = getFileChooserIntent()

        // Set your required file type
        intent!!.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image File "), requestCode)
    }

    private fun getFileChooserIntent(): Intent? {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        return intent
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (this.requestCode == requestCode && data != null) {
            imageUri = data.data!!
            addScreenshotTextView.visibility = View.GONE
            addScreenshotSmallImageView.visibility = View.GONE
            addScreenshotLargeImageView.visibility = View.VISIBLE
            CustomToast.toastIt(applicationContext, "Touch again to change image")

        }
    }

    private fun openEmail() {
        try {
            val systemInfo = SystemInfo(this)

            val emailIntent = Intent(Intent.ACTION_SEND)

            if (imageUri != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            }
            emailIntent.type = "image/*"
            val to = arrayOf("sudoajay@gmail.com")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback About DNS App")
            emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                "${feedbackEditText.text} ${systemInfo.createTextForEmail()}"
            )
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        } catch (e: Exception) {

        }

    }
    private fun callCustomSystemInfo() {
        val ft = supportFragmentManager.beginTransaction()
        val systemInfoDialog = SystemInfoDialog()
        systemInfoDialog.show(ft, "dialog")
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
