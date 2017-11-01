package net.techbrewery.jackie.socket

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManagerService
import net.techbrewery.jackie.Configuration
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket


/**
 * Created by Jacek Kwiecień on 01.11.2017.
 */
class Server {

    private val serverSocket = ServerSocket(Configuration.PORT)

    private var connectionThread: Thread? = null
    private var receiverThread: Thread? = null

    val service = PeripheralManagerService()
    var gpio: Gpio? = null

    fun start() {
        gpio = service.openGpio("GPIO_37")
        gpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
        gpio?.setActiveType(Gpio.ACTIVE_LOW)

        connectionThread = Thread(Runnable {
            Timber.i("Server is waiting . . .")
            val socket = serverSocket.accept()
            Timber.i("Client with IP: ${socket.inetAddress.hostAddress} connected")
            connectionThread = null
            startReceiverThread(socket)
        })
        connectionThread?.start()
    }

    private fun startReceiverThread(socket: Socket) {
        receiverThread = Thread(Runnable {
            do {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                val message = reader.readLine()
                if (message != null) {
                    Timber.i("Controller says: $message")
                    gpio!!.value = !gpio!!.value
                }
            } while (message != "STOP")
        })
        receiverThread?.start()
    }

    //                senderThread -> {
//                    val message = "Hello. I'm Robot.\nEND".byteInputStream(StandardCharsets.UTF_8)
//                    do {
//                        val br1 = BufferedReader(InputStreamReader(message))
//                        val pr1 = PrintWriter(socket.getOutputStream(), true)
//                        val input = br1.readLine()
//                        pr1.println(input)
//                    } while (input != "END")
//                }
}