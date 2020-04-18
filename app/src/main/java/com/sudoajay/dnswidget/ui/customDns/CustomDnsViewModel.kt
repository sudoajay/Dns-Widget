package com.sudoajay.dnswidget.ui.customDns

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CustomDnsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Custom DNS Fragment"
    }
    val text: LiveData<String> = _text
}