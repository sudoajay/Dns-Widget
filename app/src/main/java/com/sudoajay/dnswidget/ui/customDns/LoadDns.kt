package com.sudoajay.dnswidget.ui.customDns

import android.content.Context
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository

class LoadDns(private var context: Context,private var dnsRepository: DnsRepository) {


    suspend fun fillDefaultData() {
        val dnsList: MutableList<Dns> = ArrayList()

//        custom dns (Enter Manually)
        dnsList.add(
            Dns(
                null, context.getString(R.string.custom_dns_enter_manually_text), "", "",
                "", "", "None", custom = true
            )
        )

        //      Google Dns Data
        dnsList.add(
            Dns(
                null, "Google DNS", "8.8.8.8", "8.8.4.4",
                "2001:4860:4860::8888", "2001:4860:4860::8844", "None", custom = false
            )
        )

//      CloudFlare Dns Data
        dnsList.add(
            Dns(
                null, "CloudFlare DNS", "1.1.1.1",
                "1.0.0.1", "2606:4700:4700::1111", "2606:4700:4700::1001", "None", custom = false
            )
        )

//      Quad9 Dns Data
        dnsList.add(
            Dns(
                null, "Quad9 DNS", "9.9.9.9",
                "149.112.112.112", "2620:fe::fe", "2620:fe::fe:9", "None", custom = false
            )
        )

//      Open Dns Data
        dnsList.add(
            Dns(
                null, "Open DNS", "208.67.222.222",
                "208.67.220.220", "2620:119:35::35", "2620:119:53::53", "None", custom = false
            )
        )

//      Comodo Secure Dns Data
        dnsList.add(
            Dns(
                null, "Comodo Secure DNS", "8.26.56.26",
                "8.26.56.26",context.getString(R.string.unspecified_text) , context.getString(R.string.unspecified_text), "None", custom = false
            )
        )

//      CleanBrowsing default Dns Data
        dnsList.add(
            Dns(
                null, "CleanBrowsing DNS", "185.228.168.9",
                "185.228.169.9", "2a0d:2a00:1::2", "2a0d:2a00:2::2", "None", custom = false
            )
        )

//      CleanBrowsing Family Dns Data
        dnsList.add(
            Dns(
                null, "CleanBrowsing DNS", "185.228.168.168",
                "185.228.168.168", "2a0d:2a00:1::", "2a0d:2a00:1::", "Family", custom = false
            )
        )

//      CleanBrowsing Adult  Dns Data
        dnsList.add(
            Dns(
                null, "CleanBrowsing DNS", "185.228.168.10",
                "185.228.169.11", "2a0d:2a00:1::1", "2a0d:2a00:2::1", "Adult", custom = false
            )
        )

//      AdGuard Default  Dns Data
        dnsList.add(
            Dns(
                null, "AdGuard DNS", "176.103.130.130",
                "176.103.130.131", "2a00:5a60::ad1:0ff", "2a00:5a60::ad2:0ff", "None", custom = false
            )
        )

//      AdGuard Family  Dns Data
        dnsList.add(
            Dns(
                null, "AdGuard DNS", "176.103.130.132",
                "176.103.130.134", "2a00:5a60::bad1:0ff", "2a00:5a60::bad2:0ff", "Family", custom = false
            )
        )

//      AdGuard Non-filtering  Dns Data
        dnsList.add(
            Dns(
                null, "AdGuard DNS", "176.103.130.136",
                "176.103.130.137", "2a00:5a60::01:ff", "2a00:5a60::02:ff", "Non-filtering", custom = false
            )
        )

//      Verisign's public DNS
        dnsList.add(
            Dns(
                null, "Verisign's public DNS", "64.6.64.6",
                "64.6.65.6", "2620:74:1b::1:1", "2620:74:1c::2:2", "None", custom = false
            )
        )

//      Alternate DNS
        dnsList.add(
            Dns(
                null, "Alternate DNS", "198.101.242.72",
                "23.253.163.53", "2001:4801:7825:103:be76:4eff:fe10:2e49",
                "2001:4801:7825:103:be76:4eff:fe10:2e49", "None", custom = false
            )
        )


        // Fill in the data base
        for(i in dnsList){
            dnsRepository.insert(i)
        }

    }
}