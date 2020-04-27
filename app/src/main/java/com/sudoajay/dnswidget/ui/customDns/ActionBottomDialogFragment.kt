package com.sudoajay.dnswidget.ui.customDns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.CustomToast


class ActionBottomDialogFragment : BottomSheetDialogFragment(),View.OnClickListener {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottomsheet_moreoption, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.share_BottomSheet).setOnClickListener(this)
        view.findViewById<View>(R.id.edit_BottomSheet).setOnClickListener(this)
        view.findViewById<View>(R.id.delete_BottomSheet).setOnClickListener(this)
        view.findViewById<View>(R.id.copy_BottomSheet).setOnClickListener(this)
    }



    override fun onClick(view: View) {
        CustomToast.toastIt(requireContext(),"Gotcha")
        dismiss()
    }


}
