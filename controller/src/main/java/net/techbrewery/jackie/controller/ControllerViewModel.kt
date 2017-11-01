package net.techbrewery.jackie.controller

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import net.techbrewery.jackie.Configuration
import net.techbrewery.jackie.ipAddress
import net.techbrewery.jackie.subnet

/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */

class ControllerViewModel(application: Application) : AndroidViewModel(application) {

    val eventLiveData: MutableLiveData<ControllerViewEvent> = MutableLiveData()
    private val controllerIp = application.ipAddress ?: ""
    private var client: Client? = null

    init {
        eventLiveData.postValue(ControllerViewEvent.ControllerIpFound(controllerIp, "Controller IP: $controllerIp"))
    }

    fun connect(robotLastIpPart: String) {
        val robotIp = "${getApplication<Application>().subnet}.$robotLastIpPart"
        client = Client("10.0.0.6", Configuration.PORT)
        client?.start()
    }

    fun disconnect() {
        //TODO
    }

    fun toggleLed() {
        client?.sendMessage("Hello!")
    }
}

