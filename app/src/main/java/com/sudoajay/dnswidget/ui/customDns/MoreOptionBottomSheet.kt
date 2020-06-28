package com.sudoajay.dnswidget.ui.customDns

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutMoreoptionBottomsheetBinding
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MoreOptionBottomSheet(var customDns: CustomDns, var dns: Dns) : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView = layoutInflater.inflate(R.layout.layout_moreoption_bottomsheet, null)
        val binding = LayoutMoreoptionBottomsheetBinding.inflate(
            layoutInflater,
            myDrawerView as ViewGroup,
            false
        )
        binding.moreOption = this
        return binding.root
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

    fun deleteItem() {
        CoroutineScope(Dispatchers.IO).launch {
            customDns.customDnsViewModel.dnsRepository.deleteRow(dns.id!!)
        }
        customDns.customDnsViewModel.filterChanges()

        dismiss()
    }

    fun editItem(){
        customDns.addCustomDns(dns , "Edit")
        dismiss()
    }
    fun copyItem(){
        customDns.addCustomDns(dns , "Copy")
        dismiss()
    }


}
