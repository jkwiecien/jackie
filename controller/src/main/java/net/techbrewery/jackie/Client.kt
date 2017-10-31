package net.techbrewery.jackie

import android.os.AsyncTask
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException

/**
 * Created by Jacek Kwiecień on 30.10.2017.
 */

class Client internal constructor(private var destinationAddress: String, private var dstPort: Int) : AsyncTask<Void, Void, String>() {
    private var response = ""

    override fun doInBackground(vararg arg0: Void): String {

        var socket: Socket? = null

        try {
            socket = Socket(destinationAddress, dstPort)

            val byteArrayOutputStream = ByteArrayOutputStream(1024)
            val buffer = ByteArray(1024)

            val inputStream = socket.getInputStream()
            var bytesRead: Int = inputStream.read(buffer)

            /*
             * notice: inputStream.read() will block if no data return
			 */
            while (bytesRead != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8")
                bytesRead = inputStream.read(buffer)
            }

        } catch (error: UnknownHostException) {
            Timber.e(error)
            response = "UnknownHostException: " + error.toString()
        } catch (error: IOException) {
            Timber.e(error)
            response = "IOException: " + error.toString()
        } finally {
            if (socket != null) {
                try {
                    socket.close()
                } catch (e: IOException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
        }
        return response
    }

}