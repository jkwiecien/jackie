package net.techbrewery.jackie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class SocketClientActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ip = ipAddress
        val ipText = "Controller IP address: $ip"
        ipAddressLabel.text = ipText
        Timber.i("Controller starting on: $ip:${Configuration.PORT}")

        //192.168.21.173:8080
        sendButton.setOnClickListener {
            Client("10.0.0.6", Configuration.PORT, responseLabel).execute()
        }
    }
}
