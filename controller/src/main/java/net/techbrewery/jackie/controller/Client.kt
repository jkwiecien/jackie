package net.techbrewery.jackie.controller

import timber.log.Timber
import java.io.PrintWriter
import java.net.Socket


/**
 * Created by Jacek Kwiecień on 01.11.2017.
 */
class Client(private val robotIp: String, private val port: Int) {

    private var pendingMessage: String? = null
    private var connectionThread: Thread? = null
    private var senderThread: Thread? = null
    var connected = false

    fun start() {
        connectionThread = Thread(Runnable {
            Timber.i("Controller is trying to connect . . .")
            val socket = Socket(robotIp, port)
            connectionThread = null
            connected = true
            Timber.i("Controller connected!")
            startSenderThread(socket)
        })
        connectionThread?.start()
    }

    private fun startSenderThread(socket: Socket) {
        senderThread = Thread(object : Runnable {

            val writer = PrintWriter(socket.getOutputStream(), true)

            override fun run() {
                do {
                    if (pendingMessage != null) {
                        val message = pendingMessage
                        pendingMessage = null
                        Timber.i("Sending message: $message")
                        writer.println(message)
                    }

                } while (connected)
            }
        })
        senderThread?.start()
    }

    fun sendMessage(message: String) {
        pendingMessage = message
    }
}