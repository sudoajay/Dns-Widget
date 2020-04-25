package com.sudoajay.dnswidget.ui.customDns

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.sudoajay.dnswidget.R


class AddCustomDnsDialog : DialogFragment(), View.OnClickListener {
    private lateinit var rootview: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview = inflater.inflate(R.layout.layout_add_custom_dns, container, false)

        mainFun()

        return rootview
    }

    private fun mainFun() { // Reference Object

        // setup dialog box
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        rootview.findViewById<ConstraintLayout>(R.id.constraintLayout).setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.tabBackgroundColor
            )
        )


        rootview.findViewById<ImageView>(R.id.close_ImageView).setOnClickListener(this)
        rootview.findViewById<Button>(R.id.cancel_Button).setOnClickListener(this)
        rootview.findViewById<Button>(R.id.ok_Button).setOnClickListener(this)


        val dns1TextInputLayout: TextInputLayout = rootview.findViewById(R.id.dns1_TextInputLayout)
        val dns2TextInputLayout: TextInputLayout = rootview.findViewById(R.id.dns2_TextInputLayout)
        val dns3TextInputLayout: TextInputLayout = rootview.findViewById(R.id.dns3_TextInputLayout)
        val dns4TextInputLayout: TextInputLayout = rootview.findViewById(R.id.dns4_TextInputLayout)
        val useDns4CheckBox: CheckBox = rootview.findViewById(R.id.useDns4_checkBox)
        val useDns6CheckBox: CheckBox = rootview.findViewById(R.id.useDns6_checkBox)

        useDns4CheckBox
            .setOnCheckedChangeListener { _, isChecked ->
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
                current.layoutParams.width = width - 10 * width / 100
            }
        } while (current!!.parent != null)
        // Request a layout to be re-done
        current!!.requestLayout()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.close_ImageView, R.id.cancel_Button -> dismiss()
            R.id.ok_Button -> {
                dismiss()
            }


        }

    }


}