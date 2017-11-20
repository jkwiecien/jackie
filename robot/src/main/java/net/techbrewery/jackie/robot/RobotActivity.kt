package net.techbrewery.jackie.robot

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_robot.*
import net.techbrewery.jackie.Configuration
import net.techbrewery.jackie.R
import net.techbrewery.jackie.camera.CameraFrameListener
import net.techbrewery.jackie.ipAddress
import timber.log.Timber


/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */
class RobotActivity : Activity(), CameraFrameListener {

    private var robot = Robot()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_robot)

        cameraViewAtRobotActivity.frameListener = this

        val serverIpAddress = ipAddress
        Timber.i("Robot starting on: $serverIpAddress:${Configuration.PORT}")
        robot.setFrameSize(cameraViewAtRobotActivity.frameWidth, cameraViewAtRobotActivity.frameHeight)
        robot.start()
    }

    override fun onPause() {
        super.onPause()
        cameraViewAtRobotActivity.pauseCamera()
    }

    override fun onFrame(data: ByteArray) {
        robot.sendFrame(data)
    }


//    private inner class SocketServerThread : Thread() {
//
//        internal var count = 0
//        internal var message = ""
//
//        override fun run() {
//            val serverSocket = ServerSocket(Configuration.PORT)
//            this@RobotActivity.serverSocket = serverSocket
//            try {
//                while (true) {
//                    // block the call until connection is created and return
//                    // Socket object
//                    val socket = serverSocket.accept()
//                    count++
//                    message += ("#" + count + " from "
//                            + socket.inetAddress + ":"
//                            + socket.port + "\n")
//
//                    Timber.i("Message; $message")
////                    activity.runOnUiThread(Runnable { activity.msg.setText(message) })
//
//                    gpio!!.value = !gpio!!.value
//
//                    val socketServerReplyThread = SocketServerReplyThread(socket, count)
//                    socketServerReplyThread.run()
//
//                }
//            } catch (error: IOException) {
//                Timber.e(error)
//            }
//        }
//    }
//
//    private inner class SocketServerReplyThread internal constructor(private val hostThreadSocket: Socket, internal var count: Int) : Thread() {
//
//        internal var message = ""
//
//        override fun run() {
//            val outputStream: OutputStream
//            val msgReply = "Hello from Robot, you are #" + count
//
//            try {
//                outputStream = hostThreadSocket.getOutputStream()
//                val printStream = PrintStream(outputStream)
//                printStream.print(msgReply)
//                printStream.close()
//
//                message += "replied: " + msgReply + "\n"
//
//                Timber.i("Reply; $message")
////                activity.runOnUiThread(Runnable { activity.msg.setText(message) })
//
//            } catch (e: IOException) {
//                // TODO Auto-generated catch block
//                e.printStackTrace()
//                message += "Something wrong! " + e.toString() + "\n"
//            }
//
////            activity.runOnUiThread(Runnable { activity.msg.setText(message) })
//        }
//    }
}