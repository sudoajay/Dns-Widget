package com.sudoajay.dnswidget.ui.setting

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.Navigation
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.TwoStatePreference
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.CustomToast


class SettingConfiguration : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val sharedPref = PreferenceManager
            .getDefaultSharedPreferences(context)


        val useDnsv4 = findPreference("useDnsv4") as TwoStatePreference?

        val useDnsv6 = findPreference("useDnsv6") as TwoStatePreference?


        useDnsv4!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                var value = true
                if (!useDnsv6!!.isChecked && !(newValue as Boolean)) {
                    value = false
                    CustomToast.toastIt(requireContext(), getString(R.string.at_least_one_dns_text))
                }
                value
            }


        useDnsv6!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any? ->
                var value = true
                if (!useDnsv4.isChecked && !(newValue as Boolean)) {
                    value = false
                    CustomToast.toastIt(requireContext(), getString(R.string.at_least_one_dns_text))
                }
                value
            }


        val useDarkTheme =
            findPreference("useDarkTheme") as Preference?
        useDarkTheme!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            //open browser or intent here
            showDarkMode()
            true
        }


        val privacyPolicy =
            findPreference("privacyPolicy") as Preference?
        privacyPolicy!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            //open browser or intent here
            openPrivacyPolicy()
            true
        }

        val reportABug =
            findPreference("reportABug") as Preference?
        reportABug!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            //open browser or intent here
            openGithubProject()
            true
        }
        val sendFeedback =
            findPreference("sendFeedback") as Preference?
        sendFeedback!!.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            //open browser or intent here
            Navigation.findNavController(requireView())
                .navigate(R.id.action_nav_settings_to_nav_send_feedback)
            true
        }

    }

    private fun openPrivacyPolicy() {
        val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
    }


    private fun openGithubProject() {
        val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
    }

    private fun showDarkMode() {
        val darkModeBottomSheet = DarkModeBottomSheet()
        darkModeBottomSheet.show(
            childFragmentManager.beginTransaction(),
            "darkModeBottomSheet"
        )

    }


}
