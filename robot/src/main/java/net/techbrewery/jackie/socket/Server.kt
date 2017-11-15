package net.techbrewery.jackie.socket

import com.google.android.things.pio.PeripheralManagerService
import com.google.android.things.pio.Pwm
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

    private val PWM_FREQUENCY = 1000.0

    private val serverSocket = ServerSocket(Configuration.PORT)

    private var connectionThread: Thread? = null
    private var receiverThread: Thread? = null

    private val service = PeripheralManagerService()
    private var leftEnginePwm: Pwm? = null
    private var rightEnginePwm: Pwm? = null
    private var leftEngineDutyCycle = 0.0
    private var rightEngineDutyCycle = 0.0

    fun start() {
        leftEnginePwm = service.openPwm("PWM2")
        leftEnginePwm?.setPwmFrequencyHz(PWM_FREQUENCY)
        leftEnginePwm?.setPwmDutyCycle(leftEngineDutyCycle)
        leftEnginePwm?.setEnabled(true)

        rightEnginePwm = service.openPwm("PWM1")
        rightEnginePwm?.setPwmFrequencyHz(PWM_FREQUENCY)
        rightEnginePwm?.setPwmDutyCycle(rightEngineDutyCycle)
        rightEnginePwm?.setEnabled(true)

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
                if (message != null && message.contains(":")) {
//                    Timber.i("message: $message")

                    val params = message.split(":")
                    var strength = params[0].toInt()
                    var angle = params[1].toInt()
                    if (angle in 180..360) angle = 90

                    var fixedAngle = angle
                    if (fixedAngle in 180..270) fixedAngle = 180
                    else if (fixedAngle in 270..360) fixedAngle = 0

                    Timber.i("fixedAngle: $fixedAngle")
                    var leftWheelMultiplier = 1.0
                    var rightWheelMultiplier = 1.0
                    when (fixedAngle) {
                        in 91..180 -> {
                            //turning left
                            var leftTurnAngle = Math.abs(90 - fixedAngle)
                            if (leftTurnAngle > 90) leftTurnAngle = 90
                            Timber.i("leftTurnAngle: $leftTurnAngle")
                            leftWheelMultiplier = (90.0 - leftTurnAngle.toDouble()) / 90.0
                        }
                        in 0..89 -> {
                            //turning right
                            var rightTurnAngle = Math.abs(0 - fixedAngle)
                            if (rightTurnAngle > 90) rightTurnAngle = 90
                            Timber.i("rightTurnAngle: $rightTurnAngle")
                            rightWheelMultiplier = (90.0 - rightTurnAngle.toDouble()) / 90.0
                        }
                    }

                    Timber.i("leftWheelMultiplier: $leftWheelMultiplier, rightWheelMultiplier: $rightWheelMultiplier")

                    if (strength > 100) strength = 100

                    leftEngineDutyCycle = strength.toDouble() * leftWheelMultiplier
                    rightEngineDutyCycle = strength.toDouble() * rightWheelMultiplier
                    Timber.i("leftEngineDutyCycle: $leftEngineDutyCycle, rightEngineDutyCycle: $rightEngineDutyCycle")

                    leftEnginePwm?.setPwmDutyCycle(leftEngineDutyCycle)
                    rightEnginePwm?.setPwmDutyCycle(rightEngineDutyCycle)
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