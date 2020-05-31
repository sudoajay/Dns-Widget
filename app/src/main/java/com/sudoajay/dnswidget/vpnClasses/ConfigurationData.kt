package com.sudoajay.dnswidget.vpnClasses

import android.content.Context
import com.sudoajay.dnswidget.helper.DnsDatabase
import com.sudoajay.dnswidget.ui.customDns.database.Dns

class ConfigurationData(context: Context) {

    private var dnsDatabase: DnsDatabase = DnsDatabase(context)

    init {
        fillDefaultData()
    }

    private fun fillDefaultData(){



//      Google Dns Data
        dnsDatabase.fillIt("Google DNS" , "8.8.8.8",
            "8.8.4.4", "2001:4860:4860::8888","2001:4860:4860::8844","None")

//      CloudFlare Dns Data
        dnsDatabase.fillIt("CloudFlare DNS" , "1.1.1.1",
            "1.0.0.1", "2606:4700:4700::1111","2606:4700:4700::1001","None")

//      Quad9 Dns Data
        dnsDatabase.fillIt("Quad9 DNS" , "9.9.9.9",
            "149.112.112.112", "2620:fe::fe","2620:fe::fe:9","None")

//      Open Dns Data
        dnsDatabase.fillIt("Open DNS" , "208.67.222.222",
            "208.67.220.220", "2620:119:35::35","2620:119:53::53","None")

//      Comodo Secure Dns Data
        dnsDatabase.fillIt("Comodo Secure DNS" , "8.26.56.26",
            "8.26.56.26", "Unspecified","Unspecified","None")

//      CleanBrowsing default Dns Data
        dnsDatabase.fillIt("CleanBrowsing DNS" , "185.228.168.9",
            "185.228.169.9", "2a0d:2a00:1::2","2a0d:2a00:2::2","None")

//      CleanBrowsing Family Dns Data
        dnsDatabase.fillIt("CleanBrowsing DNS" , "185.228.168.168",
            "185.228.168.168", "2a0d:2a00:1::","2a0d:2a00:1::","Family")

//      CleanBrowsing Adult  Dns Data
        dnsDatabase.fillIt("CleanBrowsing DNS" , "185.228.168.10",
            "185.228.169.11", "2a0d:2a00:1::1","2a0d:2a00:2::1","Adult")

//      AdGuard Default  Dns Data
        dnsDatabase.fillIt("AdGuard DNS" , "176.103.130.130",
            "176.103.130.131", "2a00:5a60::ad1:0ff","2a00:5a60::ad2:0ff","None")

//      AdGuard Family  Dns Data
        dnsDatabase.fillIt("AdGuard DNS" , "176.103.130.132",
            "176.103.130.134", "2a00:5a60::bad1:0ff","2a00:5a60::bad2:0ff","Family")

//      AdGuard Non-filtering  Dns Data
        dnsDatabase.fillIt("AdGuard DNS" , "176.103.130.136",
            "176.103.130.137", "2a00:5a60::01:ff","2a00:5a60::02:ff","Non-filtering")

//      Verisign's public DNS
        dnsDatabase.fillIt("Verisign's public DNS" , "64.6.64.6",
            "64.6.65.6", "2620:74:1b::1:1","2620:74:1c::2:2","None")

//      Alternate DNS
        dnsDatabase.fillIt("Alternate DNS" , "198.101.242.72",
            "23.253.163.53", "2001:4801:7825:103:be76:4eff:fe10:2e49",
            "2001:4801:7825:103:be76:4eff:fe10:2e49","None")

    }

    fun setDefaultData(){
        dnsDatabase.clearData()
        fillDefaultData()
    }


}