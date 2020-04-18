package com.sudoajay.dnswidget.ui.sendFeedback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SendFeedbackViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Send Feedback Fragment"
    }
    val text: LiveData<String> = _text
}