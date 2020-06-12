package com.sudoajay.dnswidget.vpnClasses

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.sudoajay.dnswidget.MainActivity
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.ConnectivityType
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import com.sudoajay.dnswidget.ui.customDns.database.DnsDao
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository
import com.sudoajay.dnswidget.ui.customDns.database.DnsRoomDatabase
import com.sudoajay.dnswidget.vpnClasses.AdVpnThread.Notify
import com.sudoajay.dnswidget.vpnClasses.NotificationChannels.onCreate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdVpnService : VpnService() {

    // Binder given to clients (notice class declaration below)
    private var mBinder: IBinder = MyBinder()
    var dnsStatus = MutableLiveData<String>()
    private lateinit var selectedDns: Dns
    private var dnsRepository: DnsRepository? = null
    private lateinit var dnsDao: DnsDao

    private lateinit var builder: NotificationCompat.Builder


    private var vpnThread: AdVpnThread? = AdVpnThread(this, object : Notify {
        override fun run(value: Int) {
            Log.e(TAG, "$value --- VPN_MSG_STATUS_UPDATE ")

            updateVpnStatus(value)
        }
    })
    private val networkChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (ConnectivityType.getNetworkProvider(context!!)) {

                context.getString(R.string.vpn_text) -> Log.i(
                    TAG,
                    "Ignoring connectivity changed for our own network"
                )
                context.getString(R.string.no_internet_text) -> {
                    Log.i(TAG, "No Network Connection")

                    if (intent!!.getBooleanExtra(
                            ConnectivityManager.EXTRA_NO_CONNECTIVITY,
                            false
                        )
                    ) {
                        Log.i(TAG, "Connectivity changed to no connectivity, wait for a network")
                        waitForNetVpn()
                    } else {
                        Log.i(TAG, "Network changed, try to reconnect")
                        reconnect()
                    }
                }
            }
        }

    }

    fun waitForNetVpn() {
        vpnThread!!.stopThread()
        updateVpnStatus(VPN_STATUS_WAITING_FOR_NETWORK)
    }

    fun reconnect() {
        updateVpnStatus(VPN_STATUS_RECONNECTING)
        restartVpnThread()
    }


    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            onCreate(this)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //        Creating Object and Initialization
        if (dnsRepository == null) {
            dnsDao = DnsRoomDatabase.getDatabase(context = applicationContext).dnsDao()
            dnsRepository = DnsRepository(application, dnsDao)
        }
        when (if (intent == null) Command.START else Command.values()[intent.getIntExtra(
            "COMMAND",
            Command.START.ordinal
        )]) {
            Command.RESUME -> {
                Log.i(TAG, "onStartCommand  Command.RESUME -  $intent")

                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()

                getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                    .putBoolean("isDnsActive", true).apply()
                startVpn((intent?.getParcelableExtra<Parcelable>("NOTIFICATION_INTENT")) as PendingIntent?)
            }
            Command.START -> {
                CoroutineScope(Dispatchers.Main).launch {
                    selectedDns =
                        withContext(Dispatchers.IO) {
                            dnsRepository!!.getDnsFromId(
                                getSharedPreferences("state", Context.MODE_PRIVATE)
                                    .getLong("id", 0)
                            )
                        }

                    Log.i(TAG, "onStartCommand  Command.START -  $intent")
                    dnsStatus.postValue(getString(R.string.connected_progress_text))

                    getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                        .putBoolean("isDnsActive", true).apply()
                    startVpn((intent!!.getParcelableExtra<Parcelable>("NOTIFICATION_INTENT")) as PendingIntent?)

                }
            }
            Command.STOP -> {
                Log.i(TAG, "onStartCommand  Command.Stop -  $intent")

                getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                    .putBoolean("isDnsActive", false).apply()
                stopVpn()
            }
            Command.PAUSE -> {
                Log.i(TAG, "onStartCommand  Command.PAUSE -  $intent")

                pauseVpn()
            }
        }
        return Service.START_STICKY
    }

    private fun pauseVpn() {
        stopVpn()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(
            NOTIFICATION_ID_STATE,
            NotificationCompat.Builder(this, NotificationChannels.SERVICE_PAUSED)
                .setSmallIcon(R.drawable.ic_share) // TODO: Notification icon
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getString(R.string.notification_paused_title))
                .setContentText(getString(R.string.notification_paused_text))
                .setContentIntent(
                    PendingIntent.getService(
                        this,
                        REQUEST_CODE_START,
                        getResumeIntent(this),
                        PendingIntent.FLAG_ONE_SHOT
                    )
                )
                .build()
        )
    }

    private fun getResumeIntent(context: Context): Intent {
        val intent = Intent(context, AdVpnService::class.java)
        intent.putExtra("COMMAND", Command.RESUME.ordinal)
        intent.putExtra(
            "NOTIFICATION_INTENT",
            PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java), 0
            )
        )
        return intent
    }


    private fun updateVpnStatus(status: Int) {

        val text = getString(vpnStatusToTextId(status))

        Log.e(TAG, "Update Vpn Status  -  $text")

        dnsStatus.postValue(text)

    }

    private fun startVpn(notificationIntent: PendingIntent?) {

        builder = NotificationCompat.Builder(this, NotificationChannels.SERVICE_RUNNING)
        DnsNotification(applicationContext).notify("Connected", builder, selectedDns)

        builder.setSmallIcon(R.drawable.ic_day_mode)

        // pass the pending intent
        if (notificationIntent != null)
            builder.setContentIntent(notificationIntent)

        updateVpnStatus(VPN_STATUS_STARTING)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            startForeground(NOTIFICATION_ID_STATE, builder.build(), 1)
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(NOTIFICATION_ID_STATE, builder.build())


        registerReceiver(
            networkChangeReceiver,
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        )

        restartVpnThread()
    }


    private fun restartVpnThread() {
        if (vpnThread == null) {
            Log.i(TAG, "restartVpnThread: Not restarting thread, could not find thread.")
            return
        }
        Log.i(TAG, "Thread Exist and Now Stopped")
        vpnThread!!.stopThread()
        vpnThread!!.startThread(selectedDns)
    }


    private fun stopVpn() {
        Log.i(TAG, "Stopping Service")
        if (vpnThread != null) vpnThread!!.stopThread()
        vpnThread = null
        try {
            unregisterReceiver(networkChangeReceiver)
        } catch (e: IllegalArgumentException) {
            Log.i(
                TAG,
                "Ignoring exception on unregistering receiver"
            )
        }
        updateVpnStatus(VPN_STATUS_STOPPED)
        stopSelf()
    }


    override fun onDestroy() {
        Log.i(TAG, "Destroyed, shutting down")
        stopVpn()
    }


    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    /**
     * Class used for the client Binder. The Binder object is responsible for returning an instance
     * of "MyService" to the client.
     */
    /**
     * Class used for the client Binder. The Binder object is responsible for returning an instance
     * of "MyService" to the client.
     */
    inner class MyBinder : Binder() {
        // Return this instance of MyService so clients can call public methods
        val service: AdVpnService
            get() =// Return this instance of MyService so clients can call public methods
                this@AdVpnService
    }

    companion object {
        const val NOTIFICATION_ID_STATE = 10
        const val REQUEST_CODE_START = 43
        const val REQUEST_CODE_PAUSE = 42
        const val VPN_STATUS_STARTING = 0
        const val VPN_STATUS_RUNNING = 1
        const val VPN_STATUS_STOPPING = 2
        const val VPN_STATUS_WAITING_FOR_NETWORK = 3
        const val VPN_STATUS_RECONNECTING = 4
        const val VPN_STATUS_RECONNECTING_NETWORK_ERROR = 5
        const val VPN_STATUS_STOPPED = 6


        private const val TAG = "VpnService"

        // TODO: Temporary Hack til refactor is done
        fun vpnStatusToTextId(status: Int): Int {
            return when (status) {
                VPN_STATUS_STARTING -> R.string.connected_progress_text
                VPN_STATUS_RUNNING -> R.string.connected_text
                VPN_STATUS_STOPPING -> R.string.not_connected_text
                VPN_STATUS_WAITING_FOR_NETWORK -> R.string.notification_waiting_for_net
                VPN_STATUS_RECONNECTING -> R.string.notification_reconnecting
                VPN_STATUS_RECONNECTING_NETWORK_ERROR -> R.string.notification_reconnecting_error
                VPN_STATUS_STOPPED -> R.string.notification_stopped

                else -> throw IllegalArgumentException("Invalid vpnStatus value ($status)")
            }
        }


//        fun checkStartVpnOnBoot(context: Context) {
//            Log.i("BOOT", "Checking whether to start ad buster on boot")
//            val config =
//                FileHelper.loadCurrentSettings(context)
//            if (config == null || !config.autoStart) {
//                return
//            }
//            if (!context.getSharedPreferences("state", Context.MODE_PRIVATE)
//                    .getBoolean("isDnsActive", false)
//            ) {
//                return
//            }
//            if (prepare(context) != null) {
//                Log.i(
//                    "BOOT",
//                    "VPN preparation not confirmed by user, changing enabled to false"
//                )
//            }
//            Log.i("BOOT", "Starting ad buster from boot")
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                onCreate(context)
//            }
//            val intent = getStartIntent(context)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(intent)
//            } else {
//                context.startService(intent)
//            }
//        }
//
//        private fun getStartIntent(context: Context): Intent {
//            val intent = Intent(context, AdVpnService::class.java)
//            intent.putExtra("COMMAND", Command.START.ordinal)
//            intent.putExtra(
//                "NOTIFICATION_INTENT",
//                PendingIntent.getActivity(
//                    context, 0,
//                    Intent(context, MainActivity::class.java), 0
//                )
//            )
//            return intent
//        }


    }

}