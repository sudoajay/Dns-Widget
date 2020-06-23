package com.sudoajay.dnswidget

import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.sudoajay.dnswidget.ui.appFilter.LoadApps
import com.sudoajay.dnswidget.ui.appFilter.dataBase.AppDao
import com.sudoajay.dnswidget.ui.appFilter.dataBase.AppRepository
import com.sudoajay.dnswidget.ui.appFilter.dataBase.AppRoomDatabase
import com.sudoajay.dnswidget.ui.customDns.LoadDns
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository
import com.sudoajay.dnswidget.ui.customDns.database.DnsRoomDatabase
import com.sudoajay.dnswidget.ui.sendFeedback.SendFeedback
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var drawerLayout: DrawerLayout

    private val ratingLink =
        "https://play.google.com/store/apps/details?id=com.sudoajay.duplication_data"

    //  Shortcut Info Id
    private val homeShortcutId = "homeShortcut"
    private val dnsShortcutId = "dnsShortcut"
    private val settingShortcutId = "settingShortcut"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        //        App Data base Configuration
        appDatabaseConfiguration()

        //        Dns Database Configuration
        dnsDatabaseConfiguration()



        val navHostFragment = nav_host_fragment as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        navGraph = graphInflater.inflate(R.navigation.mobile_navigation)
        navController = navHostFragment.navController


        val intent = intent
        val destination = when (intent.action) {
            dnsShortcutId -> R.id.nav_custom_dns
            settingShortcutId ->
                R.id.nav_settings
            else -> R.id.nav_settings
        }
        navGraph.startDestination = destination
        navController.graph = navGraph



        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_custom_dns, R.id.nav_dns_test, R.id.nav_app_filter,
            R.id.nav_share, R.id.nav_rate_us, R.id.nav_more_app
            , R.id.nav_send_feedback, R.id.nav_settings, R.id.nav_about
        ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            shortcutManager()
        }

    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun shortcutManager() {

        val shortcutManager = getSystemService<ShortcutManager>(ShortcutManager::class.java)


        val homeShortcut = ShortcutInfo.Builder(applicationContext, homeShortcutId)
            .setShortLabel(getString(R.string.action_home))
            .setLongLabel(getString(R.string.action_home))
            .setIcon(Icon.createWithResource(applicationContext, R.drawable.ic_home))
            .setIntent(
                Intent(applicationContext, MainActivity::class.java).setAction(homeShortcutId)
            )
            .build()



        val dnsShortcut =
            ShortcutInfo.Builder(applicationContext, dnsShortcutId)
                .setLongLabel(getString(R.string.add_custom_dns_text))
                .setShortLabel(getString(R.string.add_custom_dns_text))
                .setIcon(
                    Icon.createWithResource(
                        applicationContext,
                        R.drawable.ic_dns
                    )
                )
                .setIntent(
                    Intent(
                        applicationContext,
                        MainActivity::class.java
                    ).setAction(dnsShortcutId)
                )
                .build()


        val settingShortcut =
            ShortcutInfo.Builder(applicationContext, settingShortcutId)
                .setLongLabel(getString(R.string.action_setting))
                .setShortLabel(getString(R.string.action_setting))
                .setIcon(
                    Icon.createWithResource(
                        applicationContext,
                        R.drawable.ic_settings
                    )
                )
                .setIntent(
                    Intent(
                        applicationContext,
                        MainActivity::class.java
                    ).setAction(settingShortcutId)
                )
                .build()
        shortcutManager!!.dynamicShortcuts = listOf(homeShortcut, dnsShortcut, settingShortcut)
    }


    private fun appDatabaseConfiguration() {
        //        Creating Object and Initialization
        val appDao: AppDao = AppRoomDatabase.getDatabase(applicationContext).appDao()
        val appRepository = AppRepository(applicationContext, appDao)
        val loadApps = LoadApps(applicationContext, appRepository)

        CoroutineScope(Dispatchers.IO).launch {
            if (appRepository.getCount() == 0)
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

}
