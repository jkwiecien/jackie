package net.techbrewery.jackie.camera

import android.content.Context
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import timber.log.Timber
import java.io.ByteArrayOutputStream


/**
 * Created by Jacek Kwiecień on 20.11.2017.
 */
class CameraView : SurfaceView, SurfaceHolder.Callback, Camera.PreviewCallback {

    var camera: Camera? = null
        private set
    private var byteArrayOutputStream: ByteArrayOutputStream? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        camera = Camera.open()
        holder?.addCallback(this)
        holder?.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        camera?.stopPreview()

        val parameters = camera?.parameters
        val previewSizes = parameters?.supportedPreviewSizes
        val size = previewSizes?.first()
        previewSizes?.forEach { Timber.d("Preview size: ${it.width}x${it.height}") }

        if (parameters != null && size != null) {
            parameters.setPreviewSize(size.width, size.height)
            parameters.previewFormat = ImageFormat.NV21
            val lp = layoutParams
            lp.width = parameters.previewSize?.width ?: 0
            lp.height = parameters.previewSize?.height ?: 0
            layoutParams = lp
        }

        Timber.i("Starting camera: $camera")
        startCamera()

        //Configration Camera Parameter(full-size)
//        val parameters = camera?.parameters
//        parameters?.setPreviewSize(320, 240)
//        val lp = layoutParams
//        lp.width = parameters?.previewSize?.width ?: 0
//        lp.height = parameters?.previewSize?.height ?: 0
//        layoutParams = lp
//        parameters?.previewFormat = ImageFormat.NV21
//        camera?.parameters = parameters
//        // mCamera.setDisplayOrientation(90);
//        camera?.setPreviewCallback(this)
//        camera?.startPreview()
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        cleanUp()
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        camera?.setPreviewDisplay(surfaceHolder)
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        val yuvImage = YuvImage(data, ImageFormat.NV21, this.width, this.height, null)
//        Timber.d("WidthandHeight" + yuvImage.height + "::" + yuvImage.width)
        val outputStream = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, this.width, this.height), 100, outputStream)
        byteArrayOutputStream = outputStream
    }

    fun cleanUp() {
        camera?.stopPreview()
        camera?.setPreviewCallback(null)
        camera?.release()
        camera = null
    }

    fun pauseCamera() {
        camera?.stopPreview()
        camera?.setPreviewCallback(null)
    }

    fun startCamera() {
        camera?.setPreviewCallback(this)
        camera?.startPreview()
    }
}