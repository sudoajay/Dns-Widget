package com.sudoajay.dnswidget

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.sudoajay.dnswidget.helper.CustomToast


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController // don't forget to initialize

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_custom_dns, R.id.nav_dns_test,
                R.id.nav_share,R.id.nav_rate_us,R.id.nav_more_app
                ,R.id.nav_send_feedback,R.id.nav_settings,R.id.nav_about), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    private val listener = NavController.OnDestinationChangedListener { controller, destination, arguments ->
        // react on change
        // you can check destination.id or destination.label and act based on that
        when (destination.id) {
            R.id.nav_custom_dns -> CustomToast.toastIt(applicationContext,"Got Custom DNs")
            R.id.nav_share -> {
                CustomToast.toastIt(applicationContext,"Here we are")

            }
            R.id.nav_rate_us -> {
                CustomToast.toastIt(applicationContext,"Here we rate us ")
            }
            R.id.nav_more_app -> {
                CustomToast.toastIt(applicationContext,"Here we more app")
            }
        }


    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listener)

    }
        override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
