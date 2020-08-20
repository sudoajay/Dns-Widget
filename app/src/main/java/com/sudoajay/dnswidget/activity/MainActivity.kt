package com.sudoajay.dnswidget.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.CustomToast
import com.sudoajay.dnswidget.ui.appFilter.LoadApps
import com.sudoajay.dnswidget.ui.appFilter.database.AppDao
import com.sudoajay.dnswidget.ui.appFilter.database.AppRepository
import com.sudoajay.dnswidget.ui.appFilter.database.AppRoomDatabase
import com.sudoajay.dnswidget.ui.customDns.LoadDns
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository
import com.sudoajay.dnswidget.ui.customDns.database.DnsRoomDatabase
import com.sudoajay.dnswidget.ui.sendFeedback.SendFeedback
import com.sudoajay.dnswidget.ui.setting.DarkModeBottomSheet
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var drawerLayout: DrawerLayout
    private var doubleBackToExitPressedOnce = false
    private val ratingLink =
        "https://play.google.com/store/apps/details?id=com.sudoajay.duplication_data"
    private val TAG = "MainActivityClass"
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        //        App Data base Configuration
        appDatabaseConfiguration()

        //        Dns Database Configuration
        dnsDatabaseConfiguration()

        if (!intent.action.isNullOrEmpty()) {

            val navHostFragment = nav_host_fragment as NavHostFragment
            val graphInflater = navHostFragment.navController.navInflater
            navGraph = graphInflater.inflate(R.navigation.mobile_navigation)
            navController = navHostFragment.navController


            val intent = intent
            val destination = when (intent.action) {
                dnsShortcutId -> R.id.nav_custom_dns
                settingShortcutId ->
                    R.id.nav_settings
                else -> R.id.nav_home
            }
            navGraph.startDestination = destination
            navController.graph = navGraph
        }



        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val header: View = navView.getHeaderView(0)
        header.navHeading_ConstraintLayout.setBackgroundResource(if (isDarkMode(applicationContext)) R.drawable.nav_bg_night else R.drawable.nav_bg)
        if (isDarkMode(applicationContext)) {
            navView.itemBackground = getDrawable(R.drawable.drawer_item_bg_dark_mode)

        }
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_custom_dns,
                R.id.nav_dns_test,
                R.id.nav_app_filter,
                R.id.nav_share,
                R.id.nav_rate_us,
                R.id.nav_more_app
                ,
                R.id.nav_send_feedback,
                R.id.nav_settings,
                R.id.nav_about
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)




//
//                FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener(OnCompleteListener { task ->
//                if (!task.isSuccessful) {
//                    Log.w(TAG, "getInstanceId failed", task.exception)
//                    return@OnCompleteListener
//                }
//
//                // Get new Instance ID token
//                val token = task.result?.token
//
//                // Log and toast
//                val msg = getString(R.string.msg_token_fmt, token)
//                Log.d(TAG, msg)
//                CustomToast.toastIt(applicationContext, msg)
//            })
    }

    override fun onStart() {
        Log.e(TAG, " Activity - onStart ")
        super.onStart()
    }



    override fun onPause() {
        Log.e(TAG, " Activity - onPause ")

        super.onPause()
    }


    override fun onStop() {
        Log.e(TAG, " Activity - onStop ")

        super.onStop()
    }
    override fun onRestart() {
        Log.e(TAG, " Activity - onRestart ")

        super.onRestart()
    }

    override fun onDestroy() {
        Log.e(TAG, " Activity - onDestroy ")

        super.onDestroy()
    }

    private fun darkModeConfiguration() {
        nightMode_ImageView.setOnClickListener {
            showDarkMode()
        }
    }

    override fun onResume() {
        super.onResume()



//        Dark Mode Configuration
        darkModeConfiguration()
        Log.e(TAG, " Activity - onResume ")

    }

    private fun appDatabaseConfiguration() {
        //        Creating Object and Initialization
        val appDao: AppDao = AppRoomDatabase.getDatabase(applicationContext).appDao()
        val appRepository = AppRepository(applicationContext, appDao)
        val loadApps = LoadApps(applicationContext, appRepository)

        CoroutineScope(Dispatchers.IO).launch {
                loadApps.searchInstalledApps()
        }

    }


    private fun dnsDatabaseConfiguration() {
        //        Creating Object and Initialization
        val dnsDao = DnsRoomDatabase.getDatabase(applicationContext).dnsDao()
        val dnsRepository = DnsRepository(applicationContext, dnsDao)
        val loadDns = LoadDns(applicationContext, dnsRepository)

        CoroutineScope(Dispatchers.IO).launch {
            if (dnsRepository.getCount() == 0)
                loadDns.fillDefaultData()
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val handled = NavigationUI.onNavDestinationSelected(item, navController)
        when (item.itemId) {
            R.id.nav_share -> share()
            R.id.nav_rate_us -> rateUs()
            R.id.nav_more_app -> openMoreApp()
            R.id.nav_send_feedback -> startActivity(
                Intent(
                    applicationContext,
                    SendFeedback::class.java
                )
            )

        }
        drawerLayout.closeDrawer(GravityCompat.START)

        return handled
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showDarkMode() {
        val darkModeBottomSheet = DarkModeBottomSheet(homeShortcutId)
        darkModeBottomSheet.show(
            supportFragmentManager.beginTransaction(),
            "darkModeBottomSheet"
        )

    }


    private fun share() {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Link-Share")
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.shareMessage) + " - git " + ratingLink)
        startActivity(Intent.createChooser(i, "Share via"))
    }

    private fun rateUs() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(ratingLink)
        startActivity(i)
    }

    private fun openMoreApp() {
        val link = "https://play.google.com/store/apps/dev?id=5309601131127361849"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
    }


    override fun onBackPressed() {
        onBack()
    }

    private fun onBack() {
        if (doubleBackToExitPressedOnce) {
            closeApp()
            return
        }
        doubleBackToExitPressedOnce = true
        CustomToast.toastIt(applicationContext, "Click Back Again To Exit")
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    private fun closeApp() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
    }

    companion object {
        const val settingShortcutId = "settingShortcut"

        //  Shortcut Info Id
        const val homeShortcutId = "homeShortcut"
        const val dnsShortcutId = "dnsShortcut"
    }

}
