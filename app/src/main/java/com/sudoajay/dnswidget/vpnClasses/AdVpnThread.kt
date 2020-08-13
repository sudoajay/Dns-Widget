package com.sudoajay.dnswidget.vpnClasses

import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.system.ErrnoException
import android.system.Os
import android.system.OsConstants
import android.system.StructPollfd
import android.util.Log
import com.sudoajay.dnswidget.activity.MainActivity
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.ui.appFilter.database.AppDao
import com.sudoajay.dnswidget.ui.appFilter.database.AppRepository
import com.sudoajay.dnswidget.ui.appFilter.database.AppRoomDatabase
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.pcap4j.packet.IpPacket
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.net.*
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

class AdVpnThread(private val vpnService: VpnService, private val notify: Notify?) :
    Runnable, DnsPacketProxy.EventLoop {
    /* Upstream DNS servers, indexed by our IP */
    private val upstreamDnsServers =
        ArrayList<InetAddress>()

    /* Data to be written to the device */
    private val deviceWrites: Queue<ByteArray> =
        LinkedList()

    // HashMap that keeps an upper limit of packets
    private val dnsIn = WospList()

    // The object where we actually handle packets.
    private val dnsPacketProxy = DnsPacketProxy(this)

    // Watch dog that checks our connection is alive.
    private var thread: Thread? = null
    private var selectedDns: Dns? = null

    private var mBlockFd: FileDescriptor? = null
    private var mInterruptFd: FileDescriptor? = null

    fun startThread(selectedDns: Dns?) {
        this.selectedDns = selectedDns
        Log.i(TAG, "Starting Vpn Thread")
        thread = Thread(this, "AdVpnThread")
        thread!!.start()
        Log.i(TAG, "Vpn Thread started")
    }

    fun stopThread() {


        Log.i(TAG, "Stopping Vpn Thread")
        if (thread != null) thread!!.interrupt()

        closeOrWarn(mInterruptFd)

        try {
            if (thread != null) thread!!.join(2000)
        } catch (e: InterruptedException) {
            Log.w(
                TAG,
                "stopThread: Interrupted while joining thread",
                e
            )
        }
        if (thread != null && thread!!.isAlive) {
            Log.w(
                TAG,
                "stopThread: Could not kill VPN thread, it is still alive"
            )

        } else {
            thread = null
            Log.i(TAG, "Vpn Thread stopped")
        }
    }

    @Synchronized
    override fun run() {
        Log.i(TAG, "Starting")

        // Load the block list
        try {
            dnsPacketProxy.initialize(upstreamDnsServers)

        } catch (e: InterruptedException) {
            return
        }
        notify?.run(AdVpnService.VPN_STATUS_STARTING)
        var retryTimeout = MIN_RETRY_TIME
        // Try connecting the vpn continuously
        while (true) {
            var connectTimeMillis: Long = 0
            try {
                connectTimeMillis = System.currentTimeMillis()
                // If the function returns, that means it was interrupted
                runVpn()
                Log.i(TAG, "Told to stop")
                notify!!.run(AdVpnService.VPN_STATUS_STOPPING)
                break
            } catch (e: InterruptedException) {
                break
            } catch (e: VpnNetworkException) {
                // We want to filter out VpnNetworkException from out crash analytics as these
                // are exceptions that we expect to happen from network errors
                Log.w(
                    TAG,
                    "Network exception in vpn thread, ignoring and reconnecting",
                    e
                )
                // If an exception was thrown, show to the user and try again
                notify?.run(AdVpnService.VPN_STATUS_RECONNECTING_NETWORK_ERROR)
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Network exception in vpn thread, reconnecting",
                    e
                )
                //ExceptionHandler.saveException(e, Thread.currentThread(), null);
                notify?.run(AdVpnService.VPN_STATUS_RECONNECTING_NETWORK_ERROR)
            }
            if (System.currentTimeMillis() - connectTimeMillis >= RETRY_RESET_SEC * 1000) {
                Log.i(TAG, "Resetting timeout")
                retryTimeout = MIN_RETRY_TIME
            }

            // ...wait and try again
            Log.i(
                TAG,
                "Retrying to connect in " + retryTimeout + "seconds..."
            )
            try {
                Thread.sleep(retryTimeout.toLong() * 1000)
            } catch (e: InterruptedException) {
                break
            }
            if (retryTimeout < MAX_RETRY_TIME) retryTimeout *= 2
        }
        notify?.run(AdVpnService.VPN_STATUS_STOPPED)
        Log.i(TAG, "Exiting")
    }

    @Throws(
        InterruptedException::class,
        ErrnoException::class,
        IOException::class,
        VpnNetworkException::class
    )
    private fun runVpn() {
        // Allocate the buffer for a single packet.
        val packet = ByteArray(32767)

        // A pipe we can interrupt the poll() call with by closing the interruptFd end

        // A pipe we can interrupt the poll() call with by closing the interruptFd end
        val pipes = Os.pipe()
        mInterruptFd = pipes[0]
        mBlockFd = pipes[1]


        // Authenticate and configure the virtual network interface.
        try {
            configure().use { pfd ->
                // Read and write views of the tun device
                val inputStream =
                    FileInputStream(pfd.fileDescriptor)
                val outFd =
                    FileOutputStream(pfd.fileDescriptor)

                // Now we are connected. Set the flag and show the message.
                notify?.run(AdVpnService.VPN_STATUS_RUNNING)

                // We keep forwarding packets till something goes wrong.
                while (doOne(inputStream, outFd, packet));
            }
        } finally {
            closeOrWarn(mBlockFd )
        }
    }

    @Throws(
        IOException::class,
        ErrnoException::class,
        InterruptedException::class,
        VpnNetworkException::class
    )
    private fun doOne(
        inputStream: FileInputStream,
        outFd: FileOutputStream,
        packet: ByteArray
    ): Boolean {
        val deviceFd = StructPollfd()
        deviceFd.fd = inputStream.fd
        deviceFd.events = OsConstants.POLLIN.toShort()
        val blockFd = StructPollfd()
        blockFd.fd = mBlockFd
        blockFd.events = (OsConstants.POLLHUP or OsConstants.POLLERR).toShort()
        if (!deviceWrites.isEmpty()) deviceFd.events =
            deviceFd.events or OsConstants.POLLOUT.toShort()
        val polls = arrayOfNulls<StructPollfd>(2 + dnsIn.size())
        polls[0] = deviceFd
        polls[1] = blockFd
        run {
            var i = -1
            for (wosp in dnsIn) {
                i++
                polls[2 + i] = StructPollfd()
                val pollFd = polls[2 + i]
                pollFd!!.fd = ParcelFileDescriptor.fromDatagramSocket(wosp.socket).fileDescriptor
                pollFd.events = OsConstants.POLLIN.toShort()
            }
        }
        Log.d(
            TAG,
            "doOne: Polling " + polls.size + " file descriptors"
        )
        val result = poll(polls, -1)
        if (result == 0) return true

        if (blockFd.revents.toInt() != 0) {
            Log.i(TAG, "Told to stop VPN")
            return false
        }
        // Need to do this before reading from the device, otherwise a new insertion there could
        // invalidate one of the sockets we want to read from either due to size or time out
        // constraints
        run {
            var i = -1
            val iter = dnsIn.iterator()
            while (iter.hasNext()) {
                i++
                val wosp = iter.next()
                if ((polls[i + 2]!!.revents and OsConstants.POLLIN.toShort()).toInt() != 0) {
                    Log.d(
                        TAG,
                        "Read from DNS socket" + wosp.socket
                    )
                    iter.remove()
                    handleRawDnsResponse(wosp.packet, wosp.socket)
                    wosp.socket.close()
                }
            }
        }
        if ((deviceFd.revents and OsConstants.POLLOUT.toShort()).toInt() != 0) {
            Log.d(TAG, "Write to device")
            writeToDevice(outFd)
        }
        if ((deviceFd.revents and OsConstants.POLLIN.toShort()).toInt() != 0) {
            Log.d(TAG, "Read from device")
            readPacketFromDevice(inputStream, packet)
        }
        return true
    }

    /**
     * Wrapper around [Os.poll] that automatically restarts on EINTR
     * While post-Lollipop devices handle that themselves, we need to do this for Lollipop.
     *
     * @param fds     Descriptors and events to wait on
     * @param timeout Timeout. Should be -1 for infinite, as we do not lower the timeout when
     * retrying due to an interrupt
     * @return The number of fds that have events
     * @throws ErrnoException See [Os.poll]
     */
    @Throws(ErrnoException::class, InterruptedException::class)
    fun poll(fds: Array<StructPollfd?>?, timeout: Int): Int {
        while (true) {
            if (Thread.interrupted()) throw InterruptedException()
            return try {
                Os.poll(fds, timeout)
            } catch (e: ErrnoException) {
                if (e.errno == OsConstants.EINTR) continue
                throw e
            }
        }
    }

    private fun closeOrWarn(fd: FileDescriptor?): FileDescriptor? {
        try {
            if (fd != null) Os.close(fd)
        } catch (e: ErrnoException) {
            Log.e(TAG, "closeOrWarn: ${e.message}", e)
        } finally {
            return null
        }
    }

    @Throws(VpnNetworkException::class)
    private fun writeToDevice(outFd: FileOutputStream) {
        try {
            outFd.write(deviceWrites.poll()!!)
        } catch (e: IOException) {
            // TODO: Make this more specific, only for: "File descriptor closed"
            throw VpnNetworkException("Outgoing VPN output stream closed")
        }
    }

    @Throws(
        VpnNetworkException::class,
        SocketException::class
    )
    private fun readPacketFromDevice(
        inputStream: FileInputStream,
        packet: ByteArray
    ) {
        // Read the outgoing packet from the input stream.
        val length: Int
        length = try {
            inputStream.read(packet)
        } catch (e: IOException) {
            throw VpnNetworkException(
                "Cannot read from device",
                e
            )
        }
        if (length == 0) {
            // TODO: Possibly change to exception
            Log.w(TAG, "Got empty packet!")
            return
        }
        val readPacket = packet.copyOfRange(0, length)
        dnsPacketProxy.handleDnsRequest(readPacket)
    }

    @Throws(VpnNetworkException::class)
    override fun forwardPacket(
        outPacket: DatagramPacket,
        parsedPacket: IpPacket
    ) {
        val dnsSocket: DatagramSocket
        try {
            // Packets to be sent to the real DNS server will need to be protected from the VPN
            dnsSocket = DatagramSocket()
            vpnService.protect(dnsSocket)
            dnsSocket.send(outPacket)
            dnsIn.add(
                WaitingOnSocketPacket(
                    dnsSocket,
                    parsedPacket
                )
            )
        } catch (e: IOException) {
            if (e.cause is ErrnoException) {
                val errnoExc = e.cause as ErrnoException?
                if (errnoExc!!.errno == OsConstants.ENETUNREACH || errnoExc.errno == OsConstants.EPERM) {
                    throw VpnNetworkException(
                        "Cannot send message:",
                        e
                    )
                }
            }
            Log.w(
                TAG,
                "handleDnsRequest: Could not send packet to upstream",
                e
            )
            return
        }
    }

    @Throws(IOException::class)
    private fun handleRawDnsResponse(
        parsedPacket: IpPacket,
        dnsSocket: DatagramSocket
    ) {
        val datagramData = ByteArray(1024)
        val replyPacket =
            DatagramPacket(datagramData, datagramData.size)
        dnsSocket.receive(replyPacket)
        dnsPacketProxy.handleDnsResponse(parsedPacket, datagramData)
    }

    override fun queueDeviceWrite(ipOutPacket: IpPacket) {
        deviceWrites.add(ipOutPacket.rawData)
    }

    @Throws(UnknownHostException::class)
    fun newDNSServer(
        builder: VpnService.Builder,
        format: String?,
        ipv6Template: ByteArray?,
        addr: InetAddress
    ) {
        // Optimally we'd allow either one, but the forwarder checks if upstream size is empty, so
        // we really need to acquire both an ipv6 and an ipv4 subnet.
        if (addr is Inet6Address && ipv6Template == null) {
            Log.i(
                TAG,
                "newDNSServer: Ignoring DNS server $addr"
            )
        } else if (addr is Inet4Address && format == null) {
            Log.i(
                TAG,
                "newDNSServer: Ignoring DNS server $addr"
            )
        } else if (addr is Inet4Address) {
            upstreamDnsServers.add(addr)
            val alias = String.format(format!!, upstreamDnsServers.size + 1)
            Log.i(
                TAG,
                "configure: Adding DNS Server $addr as $alias"
            )
            builder.addDnsServer(alias)
            builder.addRoute(alias, 32)
            Log.e(TAG, "$alias ---- 4  Here $addr")

        } else if (addr is Inet6Address) {
            upstreamDnsServers.add(addr)
            ipv6Template!![ipv6Template.size - 1] = (upstreamDnsServers.size + 1).toByte()
            val i6addr = Inet6Address.getByAddress(ipv6Template)
            Log.i(
                TAG,
                "configure: Adding DNS Server $addr as $i6addr"
            )
            builder.addDnsServer(i6addr)
            Log.e(
                TAG,
                "$i6addr ---- 5  Here $addr"
            )

        }
    }

    private fun configurePackages(builder: VpnService.Builder) {

        CoroutineScope(Dispatchers.Main).launch {

            val appDao: AppDao = AppRoomDatabase.getDatabase(vpnService).appDao()
            val appRepository = AppRepository(vpnService, appDao)

            var allowOnVpn: MutableList<String> = mutableListOf()
            var doNotAllowOnVpn: MutableList<String> = mutableListOf()

            withContext(Dispatchers.IO) {
                allowOnVpn = appRepository.getPackageFromSelected(true)
                doNotAllowOnVpn = appRepository.getPackageFromSelected(false)
            }

            if (doNotAllowOnVpn.isEmpty()) {
                for (app in allowOnVpn) {
                    try {
                        Log.d(
                            TAG,
                            "configure: Allowing $app to use the DNS VPN"
                        )
                        builder.addAllowedApplication(app)
                    } catch (e: Exception) {
                        Log.w(TAG, "configure: Cannot disallow", e)
                    }
                }

            } else {
                for (app in doNotAllowOnVpn) {
                    try {
                        Log.d(
                            TAG,
                            "configure: Disallowing $app from using the DNS VPN"
                        )
                        builder.addDisallowedApplication(app)
                    } catch (e: Exception) {
                        Log.w(TAG, "configure: Cannot disallow", e)
                    }
                }
            }


        }
    }

    @Throws(VpnNetworkException::class)
    private fun configure(): ParcelFileDescriptor {
        Log.i(TAG, "Configuring$this")

        // Configure a builder while parsing the parameters.
        val builder = vpnService.Builder()

        var format: String? = null

        // Determine a prefix we can use. These are all reserved prefixes for example
        // use, so it's possible they might be blocked.
        for (prefix in arrayOf(
            "192.0.2",
            "198.51.100",
            "203.0.113"
        )) {
            try {
                builder.addAddress("$prefix.1", 24)
                Log.e(TAG, "$prefix.1 ---- 1")
            } catch (e: IllegalArgumentException) {
                continue
            }
            format = "$prefix.%d"
            break
        }

        // For fancy reasons, this is the 2001:db8::/120 subnet of the /32 subnet reserved for
        // documentation purposes. We should do this differently. Anyone have a free /120 subnet
        // for us to use?
        val ipv6Template = byteArrayOf(
            32,
            1,
            13,
            (184 and 0xFF).toByte(),
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            0
        )
        //

        // Add configured DNS servers
        upstreamDnsServers.clear()
        try {
            Log.e(
                TAG,
                selectedDns!!.dnsName + " ---- custom dns   "
            )
            newDNSServer(
                builder,
                format,
                ipv6Template,
                InetAddress.getByName(getWorkingDNSInfo())
            )
        } catch (e: Exception) {
            Log.e(
                TAG,
                "configure: Cannot add custom DNS server",
                e
            )
        }


        builder.setBlocking(true)

        // Allow applications to bypass the VPN
        builder.allowBypass()

        // Explictly allow both families, so we do not block
        // traffic for ones without DNS servers (issue 129).
        builder.allowFamily(OsConstants.AF_INET)
        builder.allowFamily(OsConstants.AF_INET6)
        configurePackages(builder)

        // Create a new interface using the builder and save the parameters.
        val pfd = builder
            .setSession(vpnService.getString(R.string.app_name))
            .setConfigureIntent(
                PendingIntent.getActivity(
                    vpnService, 1, Intent(vpnService, MainActivity::class.java),
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            ).establish()
        Log.i(TAG, "Configured")
        return pfd
    }

    private fun getWorkingDNSInfo(): String {
        val dnsList = listOf(
            selectedDns!!.dns1,
            selectedDns!!.dns2,
            selectedDns!!.dns3,
            selectedDns!!.dns4
        )
        for (i in dnsList) {
            if  (i.isNotEmpty()) return  i
        }
        return ""
    }


    interface Notify {
        fun run(value: Int)
    }

    internal class VpnNetworkException : Exception {
        constructor(s: String?) : super(s)
        constructor(s: String?, t: Throwable?) : super(s, t)
    }

    /**
     * Helper class holding a socket, the packet we are waiting the answer for, and a time
     */
    private class WaitingOnSocketPacket internal constructor(
        val socket: DatagramSocket,
        val packet: IpPacket
    ) {
        private val time: Long = System.currentTimeMillis()
        fun ageSeconds(): Long {
            return (System.currentTimeMillis() - time) / 1000
        }

    }

    /**
     * Queue of WaitingOnSocketPacket, bound on time and space.
     */
    private class WospList : Iterable<WaitingOnSocketPacket?> {
        private val list =
            LinkedList<WaitingOnSocketPacket>()

        fun add(wosp: WaitingOnSocketPacket) {
            if (list.size > DNS_MAXIMUM_WAITING) {
                Log.d(
                    TAG,
                    "Dropping socket due to space constraints: " + list.element().socket
                )
                list.element().socket.close()
                list.remove()
            }
            while (!list.isEmpty() && list.element()
                    .ageSeconds() > DNS_TIMEOUT_SEC
            ) {
                Log.d(
                    TAG,
                    "Timeout on socket " + list.element().socket
                )
                list.element().socket.close()
                list.remove()
            }
            list.add(wosp)
        }

        override fun iterator(): MutableIterator<WaitingOnSocketPacket> {
            return list.iterator()
        }

        fun size(): Int {
            return list.size
        }
    }

    companion object {
        private const val TAG = "AdVpnThread"
        private const val MIN_RETRY_TIME = 5
        private const val MAX_RETRY_TIME = 2 * 60

        /* If we had a successful connection for that long, reset retry timeout */
        private const val RETRY_RESET_SEC: Long = 60

        /* Maximum number of responses we want to wait for */
        private const val DNS_MAXIMUM_WAITING = 1024
        private const val DNS_TIMEOUT_SEC: Long = 10

    }

}