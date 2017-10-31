package net.techbrewery.jackie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class SocketClientActivity : AppCompatActivity() {

    private var subnet = ""
    private var robotIpEnd = 0
    private var controllerIp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        controllerIp = ipAddress ?: ""
        Timber.i("Controller starting on: $controllerIp:${Configuration.PORT}")
        val controllerIpText = "Controller IP address: $controllerIp"
        controllerIpAddressLabel.text = controllerIpText

        val controllerSplitIp = controllerIp.split(".").toMutableList()
        controllerSplitIp.removeAt(controllerSplitIp.size - 1)
        controllerSplitIp.forEach {
            subnet += "$it."
        }

        robotIpEndInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                robotIpEnd = text.toString().toInt()
                updateRobotIpAddressLabel()
            }

        })

        //192.168.21.173:8080
        sendButton.setOnClickListener {
            Client(getRobotIp(), Configuration.PORT).execute()
        }
    }

    private fun getRobotIp(): String {
        return "$subnet$robotIpEnd"
    }

    private fun updateRobotIpAddressLabel() {
        val text = "Robot IP address: ${getRobotIp()}"
        robotIpAddressLabel.text = text
    }
}
