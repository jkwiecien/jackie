package net.techbrewery.jackie.controller

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import net.techbrewery.jackie.Configuration
import net.techbrewery.jackie.controller.remote.VideoListener
import net.techbrewery.jackie.ipAddress
import net.techbrewery.jackie.subnet

/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */

class ControllerViewModel(application: Application) : AndroidViewModel(application), VideoListener {

    val eventLiveData: MutableLiveData<ControllerViewEvent> = MutableLiveData()
    private val controllerIp = application.ipAddress ?: ""
    private var controller: Controller? = null

    init {
        eventLiveData.postValue(ControllerViewEvent.ControllerIpFound(controllerIp, "Controller IP: $controllerIp"))
    }

    fun connect(robotLastIpPart: String) {
        val robotIp = "${getApplication<Application>().subnet}.$robotLastIpPart"
//        controller = Controller("10.0.0.6", Configuration.PORT)
        controller = Controller("192.168.21.17", Configuration.PORT, this)
        controller?.start()
    }

    fun disconnect() {
        //TODO
    }

    fun toggleLed() {
        controller?.sendMessage("Hello!")
    }

    fun sendMovementParams(angle: Int, strength: Int) {
        controller?.sendMessage("$strength:$angle")
    }

    override fun onVideoFrameReceived(bitmap: Bitmap) {
        eventLiveData.postValue(ControllerViewEvent.VideoFrameReceived(bitmap))
    }
}

