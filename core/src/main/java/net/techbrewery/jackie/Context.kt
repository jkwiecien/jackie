package net.techbrewery.jackie

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import timber.log.Timber
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder


/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */

val Context.subnet: String?
    get() {
        val ip = ipAddress
        return if (ip != null) {
            var subnet = ""
            val controllerSplitIp = ip.split(".").toMutableList()
            controllerSplitIp.removeAt(controllerSplitIp.size - 1)
            controllerSplitIp.forEachIndexed { index, ipPart ->
                subnet += ipPart
                if (index < controllerSplitIp.size - 1) subnet += "."
            }
            subnet
        } else {
            null
        }
    }

val Context.ipAddress: String?
    get() {
        val wifiManager = this.getSystemService(WIFI_SERVICE) as WifiManager
        var ipAddress = wifiManager.connectionInfo.ipAddress

        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            ipAddress = Integer.reverseBytes(ipAddress)
        }

        val ipByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray()

        return try {
            InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            Timber.e("Unable to get host address.")
            null
        }
    }
