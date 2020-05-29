package com.sudoajay.dnswidget.ui.appFilter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutFilterBottomSheetBinding
import com.sudoajay.dnswidget.helper.CustomToast


class FilterBottomSheet : BottomSheetDialogFragment() {

    private var binding: LayoutFilterBottomSheetBinding? = null
    private var isSelectedBottomSheetFragment: IsSelectedBottomSheetFragment? = null

    interface IsSelectedBottomSheetFragment {
        fun handleDialogClose()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.layout_filter_bottom_sheet, container, false)
        binding!!.bottomSheet = this
        isSelectedBottomSheetFragment = activity as IsSelectedBottomSheetFragment?

        return binding!!.root
    }


    fun isValue(key: String, defaultValue: String): Boolean {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getString(key, defaultValue).toString() == defaultValue
    }

    fun setValue(key: String, value: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(key, value).apply()
        isSelectedBottomSheetFragment!!.handleDialogClose()
        dismiss()
    }

    fun isVisible(key: String): Boolean {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getBoolean(key, true)
    }

    private fun setVisible(key: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(key, !isVisible(key)).apply()
    }

    fun setUpVisible(key: String) {
        setVisible(key)

        if (!isVisible(getString(R.string.menu_system_apps)) && !isVisible(getString(R.string.menu_user_apps))) {
            CustomToast.toastIt(requireContext(), getString(R.string.at_least_one_item_text))
            setVisible(key)
        } else {
            isSelectedBottomSheetFragment!!.handleDialogClose()
            dismiss()
        }
    }
}

