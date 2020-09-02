package com.sudoajay.dnswidget.ui.customDns

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import androidx.core.view.isNotEmpty
import androidx.core.widget.CompoundButtonCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputLayout
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutAddCustomDnsBinding
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddCustomDnsDialog(
    private var customDnsViewModel: CustomDnsViewModel,
    private var dns: Dns?,
    private var type: String
) : DialogFragment() {
    private lateinit var binding: LayoutAddCustomDnsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.layout_add_custom_dns,
            null,
            false
        )
        binding.dialog = this


        mainFun()


        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun mainFun() { // Reference Object

        // setup dialog box
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        val dns1TextInputLayout: TextInputLayout = binding.dns1TextInputLayout
        val dns2TextInputLayout: TextInputLayout = binding.dns2TextInputLayout
        val dns3TextInputLayout: TextInputLayout = binding.dns3TextInputLayout
        val dns4TextInputLayout: TextInputLayout = binding.dns4TextInputLayout
        val useDns4CheckBox: CheckBox = binding.useDns4CheckBox
        val useDns6CheckBox: CheckBox = binding.useDns6CheckBox

        if (dns != null) {

            if(type == "Edit"){
                binding.nameTextInputLayout.editText!!.setText(dns!!.dnsName)

            }else {
                binding.nameTextInputLayout.editText!!.setText(dns!!.dnsName + " (Custom) ")
            }


            dns1TextInputLayout.editText!!.setText(dns!!.dns1)
            dns2TextInputLayout.editText!!.setText(dns!!.dns2)

            dns3TextInputLayout.editText!!.setText(dns!!.dns3)
            dns4TextInputLayout.editText!!.setText(dns!!.dns4)


        }

        useDns4CheckBox
            .setOnCheckedChangeListener { _, isChecked ->
                setVisibleDNSv4(isChecked)
                if (isChecked) {
                    useDns6CheckBox.isEnabled = true
                    useDns6CheckBox.alpha = 1f
                    CompoundButtonCompat.setButtonTintList(
                        useDns6CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                    )

                    dns1TextInputLayout.visibility = View.VISIBLE
                    dns2TextInputLayout.visibility = View.VISIBLE
                } else {
                    useDns6CheckBox.isEnabled = false
                    useDns6CheckBox.alpha = .5f
                    CompoundButtonCompat.setButtonTintList(
                        useDns6CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.unCheckedColor)
                        )
                    )

                    dns1TextInputLayout.visibility = View.GONE
                    dns2TextInputLayout.visibility = View.GONE
                }

            }


        useDns6CheckBox
            .setOnCheckedChangeListener { _, isChecked ->
                setVisibleDNSv6(isChecked)
                if (isChecked) {
                    useDns4CheckBox.isEnabled = true
                    useDns4CheckBox.alpha = 1f

                    CompoundButtonCompat.setButtonTintList(
                        useDns4CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                    )
                    dns3TextInputLayout.visibility = View.VISIBLE
                    dns4TextInputLayout.visibility = View.VISIBLE
                } else {
                    useDns4CheckBox.isEnabled = false
                    useDns4CheckBox.alpha = .5f

                    CompoundButtonCompat.setButtonTintList(
                        useDns4CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.unCheckedColor)
                        )
                    )
                    dns3TextInputLayout.visibility = View.GONE
                    dns4TextInputLayout.visibility = View.GONE
                }

            }

        useDns4CheckBox.isChecked = isVisibleDNSv4()
        useDns6CheckBox.isChecked = isVisibleDNSv6()

    }


    fun saveDnsDismiss() {
        if (!isError()) {
            CoroutineScope(Dispatchers.IO).launch {
                if (type != "Edit") {
                    customDnsViewModel.dnsRepository.insert(
                        Dns(
                            null,
                            binding.nameTextInputLayout.editText!!.text.toString(),
                            setText(binding.dns1TextInputLayout),
                            setText(binding.dns2TextInputLayout),
                            setText(binding.dns3TextInputLayout),
                            setText(binding.dns4TextInputLayout),
                            "None",
                            custom = true
                        )
                    )
                } else {
                    customDnsViewModel.dnsRepository.updateDns(
                        dns!!.id!!, binding.nameTextInputLayout.editText!!.text.toString(),
                        setText(binding.dns1TextInputLayout),
                        setText(binding.dns2TextInputLayout),
                        setText(binding.dns3TextInputLayout),
                        setText(binding.dns4TextInputLayout)
                    )
                }

            }
            customDnsViewModel.filterChanges()
            dismiss()
        }
    }

    private fun isError(): Boolean {
        if (binding.nameTextInputLayout.editText!!.text.isNotEmpty()) {
            if (binding.useDns4CheckBox.isChecked) {

                if ((binding.useDns6CheckBox.isChecked && addUnspecified(binding.dns1TextInputLayout) && addUnspecified(
                        binding.dns2TextInputLayout)
                            && addUnspecified(binding.dns3TextInputLayout) && addUnspecified(
                        binding.dns4TextInputLayout
                    ))
                    || (!binding.useDns6CheckBox.isChecked && addUnspecified(binding.dns1TextInputLayout) && addUnspecified(
                        binding.dns2TextInputLayout
                    ))
                ) {
                    binding.nameTextInputLayout.isErrorEnabled = true
                    binding.dns1TextInputLayout.error =
                        getString(R.string.please_enter_dns_value_text)
                    binding.dns3TextInputLayout.error = null
                    binding.dns3TextInputLayout.isErrorEnabled = false
                    return true
                }
            } else {

                if (addUnspecified(binding.dns3TextInputLayout) && addUnspecified(
                        binding.dns4TextInputLayout
                    )
                ) {
                    binding.nameTextInputLayout.isErrorEnabled = true
                    binding.dns3TextInputLayout.error =
                        getString(R.string.please_enter_dns_value_text)
                    binding.dns1TextInputLayout.error = null
                    binding.dns1TextInputLayout.isErrorEnabled = false
                    return true
                }

            }

        } else {
            binding.dns1TextInputLayout.error = null
            binding.dns1TextInputLayout.isErrorEnabled = false
            binding.dns3TextInputLayout.error = null
            binding.dns3TextInputLayout.isErrorEnabled = false

            binding.nameTextInputLayout.isErrorEnabled = true
            binding.nameTextInputLayout.error = getString(R.string.please_enter_dns_name_text)

            return true
        }
        return false
    }

    private fun addUnspecified(layout: TextInputLayout): Boolean {
        return layout.editText!!.text.toString().isEmpty()
                || layout.editText!!.text.toString() == requireContext().getString(R.string.unspecified_text)
    }


    private fun isVisibleDNSv4(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context).getBoolean("useDnsv4", true)
    }

    private fun setVisibleDNSv4(value: Boolean) {
        PreferenceManager
            .getDefaultSharedPreferences(context).edit().putBoolean("useDnsv4", value).apply()
    }

    private fun isVisibleDNSv6(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context).getBoolean("useDnsv6", false)
    }


    private fun setVisibleDNSv6(value: Boolean) {
        PreferenceManager
            .getDefaultSharedPreferences(context).edit().putBoolean("useDnsv6", value).apply()
    }

    private fun setText(textInputLayout: TextInputLayout): String {
        return if (textInputLayout.isNotEmpty()) textInputLayout.editText!!.text.toString() else requireContext().getString(
            R.string.unspecified_text
        )
    }


    override fun onStart() { // This MUST be called first! Otherwise the view tweaking will not be present in the displayed Dialog (most likely overriden)
        super.onStart()
        forceWrapContent(this.view)
    }

    private fun forceWrapContent(v: View?) { // Start with the provided view
        var current = v
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        // Travel up the tree until fail, modifying the LayoutParams
        do { // Get the parent
            val parent = current!!.parent
            // Check if the parent exists
            if (parent != null) { // Get the view
                current = try {
                    parent as View
                } catch (e: ClassCastException) { // This will happen when at the top view, it cannot be cast to a View
                    break
                }
                // Modify the layout
                current!!.layoutParams.width = width - 10 * width / 100
            }
        } while (current!!.parent != null)
        // Request a layout to be re-done
        current!!.requestLayout()
    }



}