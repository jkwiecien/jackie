package net.techbrewery.jackie.p2p

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import timber.log.Timber

/**
 * Created by Jacek Kwiecień on 26.10.2017.
 */
class DiscoveryActivity : Activity() {

    private val intentFilter = IntentFilter()
    private var p2pManager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: P2PBroadcastReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        p2pManager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
        channel = p2pManager?.initialize(this, mainLooper, null)
    }

    override fun onResume() {
        super.onResume()
        Timber.i("Registering reciver")
        receiver = P2PBroadcastReceiver()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        Timber.i("Unregistering reciver")
        unregisterReceiver(receiver)
    }

    private inner class P2PBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Timber.i("Wi-Fi P2P state ENABLED = TRUE")
//                    activity.setIsWifiP2pEnabled(true)
                } else {
                    Timber.i("Wi-Fi P2P state ENABLED = FALSE")
//                    activity.setIsWifiP2pEnabled(false)
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
                Timber.i("Peer list has changed")
                // The peer list has changed!  We should probably do something about
                // that.

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
                Timber.i("Connection state changed")
                // Connection state changed!  We should probably do something about
                // that.

            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
                val device = intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE) as WifiP2pDevice
                Timber.i("Device changed action: $device")
            }
        }
    }
}