package net.techbrewery.jackie.camera

/**
 * Created by Jacek Kwiecień on 20.11.2017.
 */
interface CameraFrameListener {

    fun onFrame(data: ByteArray)
}