package net.techbrewery.jackie.socket

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import net.techbrewery.jackie.Configuration
import net.techbrewery.jackie.ipAddress
import timber.log.Timber
import java.io.IOException
import java.io.OutputStream
import java.io.PrintStream
import java.net.ServerSocket
import java.net.Socket


/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */
class SocketServerActivity : Activity() {

    private var serverSocket: ServerSocket? = null
    val handler = Handler()
    val service = PeripheralManagerService()
    var gpio: Gpio? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serverIpAddress = ipAddress
        Timber.i("Server starting on: $serverIpAddress:${Configuration.PORT}")

        val socketServerThread = Thread(SocketServerThread())
        socketServerThread.start()

        gpio = service.openGpio("GPIO_37")
        gpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        gpio?.setActiveType(Gpio.ACTIVE_LOW)
    }

    override fun onDestroy() {
        super.onDestroy()
        serverSocket?.close()
    }

    private inner class SocketServerThread : Thread() {

        internal var count = 0
        internal var message = ""

        override fun run() {
            val serverSocket = ServerSocket(Configuration.PORT)
            this@SocketServerActivity.serverSocket = serverSocket
            try {
                while (true) {
                    // block the call until connection is created and return
                    // Socket object
                    val socket = serverSocket.accept()
                    count++
                    message += ("#" + count + " from "
                            + socket.inetAddress + ":"
                            + socket.port + "\n")

                    Timber.i("Message; $message")
//                    activity.runOnUiThread(Runnable { activity.msg.setText(message) })

                    gpio!!.value = !gpio!!.value

                    val socketServerReplyThread = SocketServerReplyThread(socket, count)
                    socketServerReplyThread.run()

                }
            } catch (error: IOException) {
                Timber.e(error)
            }
        }
    }

    private inner class SocketServerReplyThread internal constructor(private val hostThreadSocket: Socket, internal var count: Int) : Thread() {

        internal var message = ""

        override fun run() {
            val outputStream: OutputStream
            val msgReply = "Hello from Server, you are #" + count

            try {
                outputStream = hostThreadSocket.getOutputStream()
                val printStream = PrintStream(outputStream)
                printStream.print(msgReply)
                printStream.close()

                message += "replied: " + msgReply + "\n"

                Timber.i("Reply; $message")
//                activity.runOnUiThread(Runnable { activity.msg.setText(message) })

            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                message += "Something wrong! " + e.toString() + "\n"
            }

//            activity.runOnUiThread(Runnable { activity.msg.setText(message) })
        }
    }
}