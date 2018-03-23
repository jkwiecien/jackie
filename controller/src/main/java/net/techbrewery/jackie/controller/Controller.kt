package net.techbrewery.jackie.controller

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import net.techbrewery.jackie.Configuration
import net.techbrewery.jackie.controller.remote.VideoListener
import timber.log.Timber
import java.io.DataInputStream
import java.io.PrintWriter
import java.net.Socket


/**
 * Created by Jacek Kwiecień on 01.11.2017.
 */
class Controller(private val robotIp: String, private val port: Int, private val videoListener: VideoListener) {

    private var pendingMessage: String? = null
    private var connectionThread: Thread? = null
    private var senderThread: Thread? = null
    private var receiverThread: Thread? = null
    var connected = false
    private val bitmapOptions: BitmapFactory.Options by lazy {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        options
    }

    fun start() {
        connectionThread = Thread(Runnable {
            Timber.i("Controller is trying to connect . . .")
            val socket = Socket(robotIp, port)
            connectionThread = null
            connected = true
            Timber.i("Controller connected!")
            startSenderThread(socket)
            startReceiverThread(socket)
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

    private fun startReceiverThread(socket: Socket) {
        val socketInputStream = socket.getInputStream()
        val dataInputStream = DataInputStream(socketInputStream)

        receiverThread = Thread(Runnable {
            while (connected) {
                val token = dataInputStream.readInt()
                if (token == Configuration.VIDEO_TOKEN) {
                    if (dataInputStream.readUTF() == "#@@#") {
                        val imageLength = dataInputStream.readInt()
                        val buffer = ByteArray(imageLength)
                        var length = 0
                        while (length < imageLength) {
                            length += dataInputStream.read(buffer, length, imageLength - length)
                            val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size, bitmapOptions)
                            videoListener.onVideoFrameReceived(bitmap)
                        }
                    }
                } else {
                    Timber.w("Skipping dirty bytes")
                }
            }
        })
        receiverThread?.start()
    }

    fun sendMessage(message: String) {
        pendingMessage = message
    }
}