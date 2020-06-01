package com.sudoajay.dnswidget.ui.dnsTest


import java.net.InetAddress
import java.net.UnknownHostException

class PingInfo() {

    companion object {
        fun run(mHost: String): String {
            var value: String = "-"
            try {
                val dest = InetAddress.getByName(mHost)
                val ping = Ping(dest, object : Ping.PingListener {
                    override fun onPing(timeMs: Long, count: Int) {

                        value = timeMs.toString()
                    }

                    override fun onPingException(e: Exception?, count: Int) {
                        value = "-1"

                    }
                })

                ping.run()
            } catch (e: UnknownHostException) {
                value = "-1"
            }
            return value
        }

    }
}