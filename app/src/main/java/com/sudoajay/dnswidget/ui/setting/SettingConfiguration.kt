package com.sudoajay.dnswidget.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import com.sudoajay.dnswidget.R


class SettingConfiguration : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }


}
