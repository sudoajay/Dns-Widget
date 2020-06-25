package com.sudoajay.dnswidget.ui.setting

import android.content.Context
import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.dnswidget.MainActivity
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutDarkModeBottomSheetBinding


class DarkModeBottomSheet : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView = layoutInflater.inflate(R.layout.layout_dark_mode_bottom_sheet, null)
        val binding = LayoutDarkModeBottomSheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.bottomSheet = this



        return binding.root
    }


    fun getValue(): String {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getString(getString(R.string.dark_mode_text), getString(R.string.off_text)).toString()
    }

    fun setValue(value: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(getString(R.string.dark_mode_text), value).apply()

        val intent = Intent(requireContext() , MainActivity::class.java)
        intent.action = MainActivity.settingShortcutId
        requireActivity().finish()
        startActivity(intent)

    }


}

