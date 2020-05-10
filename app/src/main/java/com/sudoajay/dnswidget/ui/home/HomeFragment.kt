package com.sudoajay.dnswidget.ui.home

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputLayout
import com.jaredrummler.materialspinner.MaterialSpinner
import com.sudoajay.dnswidget.MainActivity
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.CustomToast
import com.sudoajay.dnswidget.vpnClasses.AdVpnService
import com.sudoajay.dnswidget.vpnClasses.MyVpnService
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var root: View
    private var requestDnsCode = 1


    // Boolean to check if our activity is bound to service or not
    var mIsBound: Boolean? = null

    var mService: AdVpnService? = null
    val mTAG ="ShowSomething"


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_home, container, false)

        reference()

        return root
    }

    private fun reference() {

        val dns1TextInputLayout: TextInputLayout = root.findViewById(R.id.dns1_TextInputLayout)
        val dns2TextInputLayout: TextInputLayout = root.findViewById(R.id.dns2_TextInputLayout)
        val dns3TextInputLayout: TextInputLayout = root.findViewById(R.id.dns3_TextInputLayout)
        val dns4TextInputLayout: TextInputLayout = root.findViewById(R.id.dns4_TextInputLayout)
        val materialSpinner :MaterialSpinner = root.findViewById(R.id.materialSpinner)
        val useDns4CheckBox: CheckBox = root.findViewById(R.id.useDns4_checkBox)
        val useDns6CheckBox: CheckBox = root.findViewById(R.id.useDns6_checkBox)
        val useDns4TextView:TextView = root.findViewById(R.id.useDns4_TextView)
        val useDns6TextView:TextView = root.findViewById(R.id.useDns6_TextView)
        val normalChangesButton :Button = root.findViewById(R.id.normal_Changes_Button)


        homeViewModel.dnsList().observe(viewLifecycleOwner, Observer {
            materialSpinner.setItems(it)
        })
        useDns4CheckBox
            .setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    useDns6CheckBox.isEnabled = true
                    useDns6CheckBox.alpha = 1f
                    useDns6TextView.alpha = 1f
                    CompoundButtonCompat.setButtonTintList(
                        useDns6CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                    )
                    dns1TextInputLayout.hint = getString(R.string.dns1_text)
                    dns3TextInputLayout.hint = getString(R.string.dns3_text)
                    dns2TextInputLayout.visibility = View.VISIBLE
                    dns4TextInputLayout.visibility = View.VISIBLE
                } else {
                    useDns6CheckBox.isEnabled = false
                    useDns6CheckBox.alpha = .5f
                    useDns6TextView.alpha = .5f
                    CompoundButtonCompat.setButtonTintList(
                        useDns6CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.unCheckedColor)
                        )
                    )
                    dns1TextInputLayout.hint = getString(R.string.dns3_text)
                    dns3TextInputLayout.hint = getString(R.string.dns4_text)
                    dns2TextInputLayout.visibility = View.GONE
                    dns4TextInputLayout.visibility = View.GONE
                }

            }

        useDns6CheckBox
            .setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    useDns4CheckBox.isEnabled = true
                    useDns4CheckBox.alpha = 1f
                    useDns4TextView.alpha = 1f
                    CompoundButtonCompat.setButtonTintList(
                        useDns4CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                    )
                    dns3TextInputLayout.hint = getString(R.string.dns3_text)
                    dns2TextInputLayout.visibility = View.VISIBLE
                    dns4TextInputLayout.visibility = View.VISIBLE
                } else {
                    useDns4CheckBox.isEnabled = false
                    useDns4CheckBox.alpha = .5f
                    useDns4TextView.alpha = .5f
                    CompoundButtonCompat.setButtonTintList(
                        useDns4CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.unCheckedColor)
                        )
                    )
                    dns3TextInputLayout.hint = getString(R.string.dns2_text)
                    dns2TextInputLayout.visibility = View.GONE
                    dns4TextInputLayout.visibility = View.GONE
                }

            }

        root.findViewById<Button>(R.id.customDns_Button).setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_open_custom_dns)
        }

        root.findViewById<Button>(R.id.dnsTest_Button).setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_open_dns_test)
        }


        normalChangesButton.setOnClickListener {

            val intent = VpnService.prepare(requireContext())
            Log.e("GotSomething", "reach ")
            if (intent != null) {
                Log.e("GotSomething", " Not Null ")
                startActivityForResult(intent, requestDnsCode)
            } else {
                Log.e("GotSomething", " Null ")
                onActivityResult(requestDnsCode, Activity.RESULT_OK, null)
            }

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_CANCELED)
            CustomToast.toastIt(requireContext(),getString(R.string.could_not_configure_vpn_service))

        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_OK ) {
            CustomToast.toastIt(requireContext()," Result Code Equal to Result Ok ")
            requireActivity().startService(Intent(requireContext(), AdVpnService::class.java).also { intent ->
                requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            })
        }

    }


    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            Log.d(mTAG, "ServiceConnection: connected to service.")
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            val binder = iBinder as AdVpnService.MyBinder
            mService = binder.service
            mIsBound = true
            getStatus() // return a random number from the service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(mTAG, "ServiceConnection: disconnected from service.")
            mIsBound = false
        }
    }

    /**
     * Method for listening to random numbers generated by our service class
     */
    private fun getStatus() {
        mService!!.dnsStatus.observe(this
            , Observer {
                statusDns_editText.text = it
            })
    }


}
