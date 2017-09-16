package net.techbrewery.jackie

import android.app.Activity
import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*


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

    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
                refreshCamera()
            }

            override fun surfaceDestroyed(p0: SurfaceHolder?) {
                camera?.stopPreview()
                camera?.release()
                camera = null

            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                val cameraId = findCameraId()
                cameraId?.let {
                    camera = Camera.open(cameraId)

                    val param: Camera.Parameters = camera!!.parameters

                    // modify parameter
                    param.setPreviewSize(352, 288)
                    camera!!.parameters = param
                    camera!!.setPreviewDisplay(holder)
                    camera!!.startPreview()
                }
            }
        })
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)

        refreshCamera()
    }

    private fun findCameraId(): Int? {
        val numberOfCameras = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            return i
        }
        return null
    }

    private fun refreshCamera() {
        val surface = surfaceHolder.surface
        surface?.let {
            // stop preview before making changes
            try {
                camera?.stopPreview()
            } catch (e: Exception) {
                // ignore: tried to stop a non-existent preview
                e.printStackTrace()
            }

            try {
                camera?.setPreviewDisplay(surfaceHolder)
                camera?.startPreview()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}
