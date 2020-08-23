package com.sudoajay.dnswidget.vpnClasses

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.net.ConnectivityManager
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.activity.MainActivity
import com.sudoajay.dnswidget.helper.ConnectivitySpeed
import com.sudoajay.dnswidget.helper.ConnectivityType
import com.sudoajay.dnswidget.helper.ImageUtils
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import com.sudoajay.dnswidget.ui.customDns.database.DnsDao
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository
import com.sudoajay.dnswidget.ui.customDns.database.DnsRoomDatabase
import com.sudoajay.dnswidget.ui.setting.SettingConfiguration
import com.sudoajay.dnswidget.vpnClasses.AdVpnThread.Notify
import com.sudoajay.dnswidget.vpnClasses.NotificationChannels.notificationOnCreate
import kotlinx.coroutines.*


class  AdVpnService : VpnService() {

    // Binder given to clients (notice class declaration below)
    private var mBinder: IBinder = MyBinder()
    var dnsStatus = MutableLiveData<String>()
    private lateinit var selectedDns: Dns
    private var dnsRepository: DnsRepository? = null
    private lateinit var dnsDao: DnsDao
    private lateinit var notificationBuilder : Notification.Builder

    private lateinit var notificationCompat: NotificationCompat.Builder
    private var dnsNotification:DnsNotification? = null
    private var notificationJob :Job?=null


    private var vpnThread: AdVpnThread? = AdVpnThread(this, object : Notify {
        override fun run(value: Int) {

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
                else -> Log.i(
                    TAG,
                    ConnectivityType.getNetworkProvider(context)
                )
            }
        }

    }

    fun waitForNetVpn() {
        vpnThread!!.stopThread()
        updateVpnStatus(VPN_STATUS_WAITING_FOR_NETWORK)
    }

    fun reconnect() {
        updateVpnStatus(VPN_STATUS_RECONNECTING)
        startVpn()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //        Creating Object and Initialization
        if (dnsRepository == null) {
            dnsDao = DnsRoomDatabase.getDatabase(applicationContext).dnsDao()
            dnsRepository = DnsRepository(application, dnsDao)
        }

        when (if (intent == null) Command.START else Command.values()[intent.getIntExtra(
            "COMMAND",
            Command.START.ordinal
        )]) {
            Command.RESUME -> {
                Log.i(TAG, "onStartCommand  Command.RESUME -  $intent")

                closeNotification()

                reconnect()


            }
            Command.START -> {

                CoroutineScope(Dispatchers.Main).launch {

                    selectedDns =
                        withContext(Dispatchers.IO) {
                            dnsRepository!!.getDnsFromId(
                                getSharedPreferences("state", Context.MODE_PRIVATE)
                                    .getLong("id", 1)
                            )
                        }

                    Log.e(TAG, selectedDns.dnsName + " -- get Name")
                    Log.i(TAG, "onStartCommand  Command.START -  ")
                    dnsStatus.postValue(getString(R.string.connected_progress_text))

                    getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                        .putBoolean(getString(R.string.is_dns_active_text), true).apply()
                    startVpn()
                }


            }
            Command.STOP -> {
                Log.i(TAG, "onStartCommand  Command.Stop -  ")

                getSharedPreferences("state", Context.MODE_PRIVATE).edit()
                    .putBoolean(getString(R.string.is_dns_active_text), false).apply()
                stopVpn()
            }
            Command.PAUSE -> {
                Log.i(TAG, "onStartCommand  Command.PAUSE -  ")

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
                .setSmallIcon(R.drawable.ic_dns) // TODO: Notification icon
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle(getString(R.string.notification_paused_title))
                .setContentText(getString(R.string.notification_paused_text))
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .setContentIntent(
                    getResumePendingIntent()
                )
                .build()
        )
    }

    private fun getResumePendingIntent(): PendingIntent {

        return PendingIntent.getService(
            applicationContext,
            REQUEST_CODE_START,
            Intent(applicationContext, AdVpnService::class.java)
                .putExtra("COMMAND", Command.RESUME.ordinal),
            0
        )
    }


    private fun updateVpnStatus(status: Int) {

        val text = getString(vpnStatusToTextId(status))

        Log.e(TAG, "Update Vpn Status  -  $text")

        dnsStatus.postValue(text)

    }

    private fun startVpn() {

        dnsNotification= DnsNotification(applicationContext)
            if (isNetworkSpeedNotification()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notificationBuilder =
                        Notification.Builder(applicationContext, NotificationChannels.SERVICE_RUNNING_Speed)
                } else {
                    @Suppress("DEPRECATION")
                    notificationBuilder = Notification.Builder(applicationContext)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notificationBuilder.setSmallIcon(
                        Icon.createWithBitmap(
                            ImageUtils.createBitmapFromString(
                                "0",
                                " KB"
                            )
                        )
                    )


                } else notificationBuilder.setSmallIcon(R.drawable.ic_dns_test)


                notificationBuilder.setContentIntent(createPendingIntent())


                dnsNotification!!.notifyBuilder(
                    "Connected",
                    notificationBuilder,
                    selectedDns
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    notificationJob =   startCoroutineTimer {
                        val networkSpeed = ConnectivitySpeed.getNetworkSpeed()
                        val speed = networkSpeed.subSequence(0, networkSpeed.indexOf(" ") + 1)
                        val units =
                            networkSpeed.subSequence(
                                networkSpeed.indexOf(" ") + 1,
                                networkSpeed.length
                            )

                        val bitmap =
                            ImageUtils.createBitmapFromString(speed.toString(), units.toString())
                        val icon = Icon.createWithBitmap(bitmap)
                        notificationBuilder.setSmallIcon(icon)

                        dnsNotification!!.notifyNotification(dnsNotification!!.notification!!)

                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    startForeground(NOTIFICATION_ID_STATE, notificationBuilder.build(), 1)
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForeground(NOTIFICATION_ID_STATE, notificationBuilder.build())


            } else {

                notificationCompat =
                    NotificationCompat.Builder(applicationContext, NotificationChannels.SERVICE_RUNNING_More_Option)
                notificationCompat.setSmallIcon(R.drawable.ic_dns)

                notificationCompat.setContentIntent(createPendingIntent())

               dnsNotification!!.notifyCompat(
                    "Connected",
                    notificationCompat,
                    selectedDns
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    startForeground(NOTIFICATION_ID_STATE, notificationCompat.build(), 1)
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    startForeground(NOTIFICATION_ID_STATE, notificationCompat.build())
            }

            updateVpnStatus(VPN_STATUS_STARTING)

            registerReceiver(
                networkChangeReceiver,
                IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
            )

            restartVpnThread()


    }


    private inline fun startCoroutineTimer(
        repeatMillis: Long = 1000,
        crossinline action: () -> Unit
    ) = GlobalScope.launch {
        while (true) {
            action()
            delay(repeatMillis)
        }
    }


    private fun isNetworkSpeedNotification(): Boolean {
      val str = applicationContext.resources.getStringArray(R.array.notificationValues)[0]

        return SettingConfiguration.getModifyNotification(applicationContext) == str
    }


    private fun restartVpnThread() {
        if (vpnThread == null) {
            Log.i(TAG, "restartVpnThread: Not restarting thread, could not find thread.")
            vpnThread = AdVpnThread(this, object : Notify {
                override fun run(value: Int) {
                    Log.e(TAG, "$value --- VPN_MSG_STATUS_UPDATE ")

                    updateVpnStatus(value)
                }
            })
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

        stopForeground(true)
        CoroutineScope(Dispatchers.IO).launch {
            notificationJob?.cancelAndJoin()
        }
        dnsNotification?.notificationManager?.cancelAll()
        updateVpnStatus(VPN_STATUS_STOPPED)
        stopSelf()
    }

    private fun closeNotification(){
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun createPendingIntent(): PendingIntent? {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
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
        const val REQUEST_CODE_STOP = 41
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


        fun checkStartVpnOnBoot(context: Context) {
            Log.i("BOOT", "Checking whether to start ad buster on boot")

            if (!context.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(context.getString(R.string.is_dns_active_text), false)
            ) {
                return
            }
            if (prepare(context) != null) {
                Log.i(
                    "BOOT",
                    "VPN preparation not confirmed by user, changing enabled to false"
                )
            }
            Log.i("BOOT", "Starting ad buster from boot")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationOnCreate(context)
            }
            val intent = getStartIntent(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private fun getStartIntent(context: Context): Intent {
            val intent = Intent(context, AdVpnService::class.java)
            intent.putExtra("COMMAND", Command.START.ordinal)
            return intent
        }


    }


}