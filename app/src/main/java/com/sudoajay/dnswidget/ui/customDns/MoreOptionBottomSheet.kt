package com.sudoajay.dnswidget.ui.customDns

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutMoreoptionBottomsheetBinding
import com.sudoajay.dnswidget.helper.CustomToast
import com.sudoajay.dnswidget.ui.customDns.database.Dns


class MoreOptionBottomSheet(var dns: Dns) : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView = layoutInflater.inflate(R.layout.layout_moreoption_bottomsheet_, null)
        val binding = LayoutMoreoptionBottomsheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.moreOption = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


     fun share() {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Dns")
        i.putExtra(Intent.EXTRA_TEXT, getDnsInfo())
        startActivity(Intent.createChooser(i, "Share Dns"))
        dismiss()
    }

    private fun getDnsInfo(): String {
        val str = StringBuilder()
        // your code here
        // your code here
        str.append(dns.dnsName)

        str.append("\n\nDNSv4\t\t\tDNSv6")
        str.append("\n" + dns.dns1 + "\t\t" + dns.dns3)
        str.append("\n" + dns.dns2 + "\t\t" + dns.dns4)
        return str.toString()
    }



}
