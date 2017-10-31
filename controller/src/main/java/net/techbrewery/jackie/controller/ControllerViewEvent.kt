package net.techbrewery.jackie.controller

/**
 * Created by Jacek Kwiecień on 31.10.2017.
 */
sealed class ControllerViewEvent {
    class RobotResponded(val response: String) : ControllerViewEvent()
    class ControllerIpFound(val controllerIp: String, val labelText: String) : ControllerViewEvent()
    class RobotIpFound(val robotIp: String, val labelText: String) : ControllerViewEvent()
}