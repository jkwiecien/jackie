package net.techbrewery.jackie.controller.connection

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_connection.*
import net.techbrewery.jackie.R
import net.techbrewery.jackie.controller.ControllerActivity

/**
 * Created by Jacek Kwiecień on 02.11.2017.
 */

class ConnectionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_connection, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        connectionButtonAtConnectionFragment.setOnClickListener {
            (activity as? ControllerActivity)?.connect(robotIpLastPartInputAtConnectionFragment.text.toString())
        }

        toggleLedButtonAtConnectionFragment.setOnClickListener {
            (activity as? ControllerActivity)?.toggleLed()
        }

        showRemoteButtonAtConnectionFragment.setOnClickListener {
            (activity as? ControllerActivity)?.showRemote()
        }
    }

    fun updateControllerIpLabel(text: String) {
        controllerIpAddressLabelAtConnectionFragment.text = text
    }

    fun updateRobotIpLabel(text: String) {
        robotIpAddressLabelAtConnectionFragment.text = text
    }

}