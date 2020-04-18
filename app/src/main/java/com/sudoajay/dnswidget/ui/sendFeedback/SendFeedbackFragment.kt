package com.sudoajay.dnswidget.ui.sendFeedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sudoajay.dnswidget.R

class SendFeedbackFragment : Fragment() {

    private lateinit var sendFeedbackViewModel: SendFeedbackViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        sendFeedbackViewModel =
            ViewModelProvider(this).get(SendFeedbackViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_send_feedback, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        sendFeedbackViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
