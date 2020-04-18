package com.sudoajay.dnswidget.ui.dnsTest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DnsTestViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is DNS Test Fragment"
    }
    val text: LiveData<String> = _text
}