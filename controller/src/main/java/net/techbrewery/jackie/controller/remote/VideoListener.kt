package net.techbrewery.jackie.controller.remote

import android.graphics.Bitmap

/**
 * Created by Jacek Kwiecień on 20.11.2017.
 */
interface VideoListener {
    fun onVideoFrameReceived(bitmap: Bitmap)
}