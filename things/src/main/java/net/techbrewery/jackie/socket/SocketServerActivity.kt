package net.techbrewery.jackie.socket

import android.app.Activity
import android.os.Bundle
import java.net.ServerSocket

/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */
class SocketServerActivity : Activity() {

    private var serverSocket: ServerSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}