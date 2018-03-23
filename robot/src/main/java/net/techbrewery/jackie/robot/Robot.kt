package net.techbrewery.jackie.robot

import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.Pwm
import net.techbrewery.jackie.Configuration
import timber.log.Timber
import java.io.*
import java.net.ServerSocket
import java.net.Socket


/**
 * Created by Jacek Kwiecień on 01.11.2017.
 */
class Robot {

    private val PWM_FREQUENCY = 100.0

    private val serverSocket = ServerSocket(Configuration.PORT)

    private var connectionThread: Thread? = null
    private var commandsReceiverThread: Thread? = null
    private var videoSenderThread: Thread? = null

    private val peripheralManager = PeripheralManager.getInstance()

    private var leftEnginePwm: Pwm? = null
    private var leftEnginePwmEnabled = false
    private var leftEngineDutyCycle = 0.0

    private var rightEnginePwm: Pwm? = null
    private var rightEnginePwmEnabled = false
    private var rightEngineDutyCycle = 0.0

    private var videoActive = true

    private var socketOutputStream: OutputStream? = null

    var videoDataToSend: ByteArray? = null
    private var frameWidth = 0
    private var frameHeight = 0

    fun start() {
        leftEnginePwm = peripheralManager.openPwm("PWM2")
        leftEnginePwm?.setPwmFrequencyHz(PWM_FREQUENCY)
        leftEnginePwm?.setPwmDutyCycle(leftEngineDutyCycle)
        leftEnginePwm?.setEnabled(leftEnginePwmEnabled)

        rightEnginePwm = peripheralManager.openPwm("PWM1")
        rightEnginePwm?.setPwmFrequencyHz(PWM_FREQUENCY)
        rightEnginePwm?.setPwmDutyCycle(rightEngineDutyCycle)
        rightEnginePwm?.setEnabled(rightEnginePwmEnabled)

        connectionThread = Thread(Runnable {
            Timber.i("Robot is waiting . . .")
            val socket = serverSocket.accept()
            Timber.i("Client with IP: ${socket.inetAddress.hostAddress} connected")
            connectionThread = null
            socket.keepAlive = true
            startCommandsReceiverThread(socket)
            startVideoSenderThread(socket)
        })
        connectionThread?.start()
    }

    private fun startCommandsReceiverThread(socket: Socket) {
        commandsReceiverThread = Thread(Runnable {
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

                    var leftWheelMultiplier = 1.0
                    var rightWheelMultiplier = 1.0
                    when (fixedAngle) {
                        in 91..180 -> {
                            //turning left
                            var leftTurnAngle = Math.abs(90 - fixedAngle)
                            if (leftTurnAngle > 90) leftTurnAngle = 90
//                            Timber.i("leftTurnAngle: $leftTurnAngle")
                            leftWheelMultiplier = (90.0 - leftTurnAngle.toDouble()) / 90.0
                        }
                        in 0..89 -> {
                            //turning right
                            var rightTurnAngle = Math.abs(0 - fixedAngle)
                            if (rightTurnAngle > 90) rightTurnAngle = 90
//                            Timber.i("rightTurnAngle: $rightTurnAngle")
                            rightWheelMultiplier = rightTurnAngle.toDouble() / 90.0
                        }
                    }

                    if (strength > 100) strength = 100

                    leftEngineDutyCycle = strength.toDouble() * leftWheelMultiplier
                    rightEngineDutyCycle = strength.toDouble() * rightWheelMultiplier
                    Timber.i("leftEngineDutyCycle: $leftEngineDutyCycle, rightEngineDutyCycle: $rightEngineDutyCycle")

                    leftEnginePwm?.setPwmDutyCycle(leftEngineDutyCycle)
                    rightEnginePwm?.setPwmDutyCycle(rightEngineDutyCycle)

                    val leftEnabled = leftEngineDutyCycle > 0
                    if (leftEnabled != leftEnginePwmEnabled) {
                        leftEnginePwmEnabled = leftEnabled
                        leftEnginePwm?.setEnabled(leftEnginePwmEnabled)
                    }

                    val rightEnabled = rightEngineDutyCycle > 0
                    if (rightEnabled != rightEnginePwmEnabled) {
                        rightEnginePwmEnabled = rightEnabled
                        rightEnginePwm?.setEnabled(rightEnginePwmEnabled)
                    }

                }
            } while (message != "STOP")
        })
        commandsReceiverThread?.start()
    }

    private fun startVideoSenderThread(socket: Socket) {
        val sos = socket.getOutputStream()
        socketOutputStream = sos

        videoSenderThread = Thread(Runnable {
            while (videoActive && sos != null && videoDataToSend != null) {
                val frameBuffer = ByteArrayOutputStream()
                val yuvImage = YuvImage(videoDataToSend!!, ImageFormat.NV21, frameWidth, frameHeight, null)
                yuvImage.compressToJpeg(Rect(0, 0, frameWidth, frameHeight), 100, frameBuffer)

                val outputStream = DataOutputStream(socketOutputStream)
                outputStream.writeInt(Configuration.VIDEO_TOKEN)
                outputStream.writeUTF("#@@#")
                outputStream.writeInt(frameBuffer.size())
                outputStream.writeUTF("-@@-")
                outputStream.flush()
                Timber.d("Sending frame of size: ${frameBuffer.size()}")
                outputStream.write(frameBuffer.toByteArray())
                outputStream.flush()
                Thread.sleep((1000 / 15).toLong())
            }
        })
    }

    fun setFrameSize(frameWidth: Int, frameHeight: Int) {
        this.frameWidth = frameWidth
        this.frameHeight = frameHeight
    }

    fun sendFrame(data: ByteArray) {
        videoDataToSend = data
    }
}