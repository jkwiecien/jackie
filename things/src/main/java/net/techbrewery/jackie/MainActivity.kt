package net.techbrewery.jackie

import android.app.Activity
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException


/**
 * Skeleton of an Android Things activity.
 *
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * val service = PeripheralManagerService()
 * val mLedGpio = service.openGpio("BCM6")
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
 * mLedGpio.value = true
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 *
 */
class MainActivity : Activity() {

    lateinit var surfaceViewHolder: SurfaceHolder
    var camera: Camera? = null
    var localSizes: List<Camera.Size>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceViewHolder = surfaceView.holder
        surfaceViewHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun surfaceCreated(p0: SurfaceHolder?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        surfaceViewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        val cameraId = findCamera()
        cameraId?.let {
            safeCameraOpen(cameraId)
            startCameraPreview()
        }

    }

    private fun findCamera(): Int? {
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            return i
        }
        return null
    }

    private fun safeCameraOpen(id: Int): Boolean {
        var qOpened = false

        try {
            releaseCameraAndPreview()
            camera = Camera.open(id)
            qOpened = camera != null
        } catch (e: Exception) {
            Log.e(getString(R.string.app_name), "failed to open Camera")
            e.printStackTrace()
        }

        return qOpened
    }

    private fun startCameraPreview() {
//        val localSizes = camera.parameters.supportedPreviewSizes
//        mSupportedPreviewSizes = localSizes
//        requestLayout()

        try {
            camera?.setPreviewDisplay(surfaceViewHolder)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Important: Call startPreview() to start updating the preview
        // surface. Preview must be started before you can take a picture.
        camera?.startPreview()
    }

    private fun releaseCameraAndPreview() {
//        mPreview.setCamera(null)
        camera?.release()
        camera = null
    }

}
