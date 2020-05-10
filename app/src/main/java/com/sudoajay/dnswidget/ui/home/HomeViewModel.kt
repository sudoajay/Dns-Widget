package com.sudoajay.dnswidget.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private var _dnsList: MutableLiveData<List<String>>? = null

    fun dnsList(): LiveData<List<String>> {
        if (_dnsList == null) {
            _dnsList = MutableLiveData<List<String>>()
            loadDefaultDna()
        }
        return _dnsList as MutableLiveData<List<String>>
    }

    private fun loadDefaultDna() {
        _dnsList!!.value = listOf(
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow")
    }
}