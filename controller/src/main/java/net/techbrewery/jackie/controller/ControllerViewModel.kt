package net.techbrewery.jackie.controller

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.techbrewery.jackie.Configuration
import net.techbrewery.jackie.ipAddress
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.net.Socket

/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */

class ControllerViewModel(application: Application) : AndroidViewModel(application) {

    val eventLiveData: MutableLiveData<ControllerViewEvent> = MutableLiveData()
    private val controllerIp = application.ipAddress ?: ""

    private val subnet: String by lazy {
        var subnet = ""
        val controllerSplitIp = controllerIp.split(".").toMutableList()
        controllerSplitIp.removeAt(controllerSplitIp.size - 1)
        controllerSplitIp.forEachIndexed { index, ipPart ->
            subnet += ipPart
            if (index < controllerSplitIp.size - 1) subnet += "."
        }
        subnet
    }

    private var robotIpAddressLastPart = 0
    private var socket: Socket? = null
    private val robotIp: String get() = "$subnet.$robotIpAddressLastPart"

    init {
        eventLiveData.postValue(ControllerViewEvent.ControllerIpFound(controllerIp, "Controller IP: $controllerIp"))
    }

    private fun connect(): Socket {
        socket?.close()
        socket = Socket(robotIp, Configuration.PORT)
        return socket!!
    }

    fun disconnect() {
        socket?.close()
        socket = null
    }

    fun onRobotIpAddressChanged(robotIpAddressLastPart: String) {
        this.robotIpAddressLastPart = robotIpAddressLastPart.toInt()
        eventLiveData.postValue(ControllerViewEvent.RobotIpFound(robotIp, "Robot IP: $robotIp"))
    }

    fun toggleLed() {
        val socketFlowable = Flowable.fromCallable { connect() }
        val finalFlowable =
                socketFlowable.flatMap { socket ->
                    Flowable.fromCallable {
                        var response = ""
                        val byteArrayOutputStream = ByteArrayOutputStream(1024)
                        val buffer = ByteArray(1024)

                        val inputStream = socket.getInputStream()
                        var bytesRead: Int = inputStream.read(buffer)

                        /*
                         * notice: inputStream.read() will block if no data return
                         */
                        while (bytesRead != -1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                            response += byteArrayOutputStream.toString("UTF-8")
                            bytesRead = inputStream.read(buffer)
                        }
                        response
                    }
                }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())

        finalFlowable.subscribe(
                { response ->
                    eventLiveData.postValue(ControllerViewEvent.RobotResponded(response!!))
                },
                { error ->
                    Timber.e(error)
                }
        )
    }
}


//class Client1 internal constructor(private var destinationAddress: String, private var dstPort: Int) : AsyncTask<Void, Void, String>() {
//    private var response = ""
//
//    override fun doInBackground(vararg arg0: Void): String {
//
//        var socket: Socket? = null
//
//        try {
//            socket = Socket(destinationAddress, dstPort)
//
//            val byteArrayOutputStream = ByteArrayOutputStream(1024)
//            val buffer = ByteArray(1024)
//
//            val inputStream = socket.getInputStream()
//            var bytesRead: Int = inputStream.read(buffer)
//
//            /*
//             * notice: inputStream.read() will block if no data return
//			 */
//            while (bytesRead != -1) {
//                byteArrayOutputStream.write(buffer, 0, bytesRead);
//                response += byteArrayOutputStream.toString("UTF-8")
//                bytesRead = inputStream.read(buffer)
//            }
//
//        } catch (error: UnknownHostException) {
//            Timber.e(error)
//            response = "UnknownHostException: " + error.toString()
//        } catch (error: IOException) {
//            Timber.e(error)
//            response = "IOException: " + error.toString()
//        } finally {
//            if (socket != null) {
//                try {
//                    socket.close()
//                } catch (e: IOException) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace()
//                }
//            }
//        }
//        return response
//    }
//
//}