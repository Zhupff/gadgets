package zhupff.gadgets.qrcode.test

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.StreamConfigurationMap
import android.media.ImageReader
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityCompat
import com.google.zxing.ReaderException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import zhupff.gadgets.logger.logV
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.min

class ScanFragment : androidx.fragment.app.Fragment(), SurfaceHolder.Callback, TextureView.SurfaceTextureListener {

    private val TAG = "ScanFragment(${hashCode()})"

    companion object {

        fun newInstance(
            @LayoutRes layoutId: Int,
        ): ScanFragment {
            val fragment = ScanFragment()
            fragment.arguments = Bundle().also { it.putInt("layout_id", layoutId) }
            return fragment
        }
    }

    private val cameraPermission = CameraPermission { granted ->
        if (granted) {
            tryToStartPreview()
        }
    }

    private var camera: CameraDevice? = null
    private val cameraDeviceStateCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            this@ScanFragment.camera = camera
            val surface = if (scanSurface != null) {
                scanSurface!!.holder.surface
            } else if (scanTexture != null) {
                Surface(scanTexture!!.surfaceTexture!!)
            } else throw IllegalStateException("ScanPreviewer not found")
            captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureRequestBuilder?.addTarget(surface)
            captureRequestBuilder?.addTarget(imageReader!!.surface)
            camera.createCaptureSession(listOf(surface, imageReader!!.surface), captureSessionCallback, null)
        }
        override fun onDisconnected(camera: CameraDevice) {
            tryToStopPreview()
        }
        override fun onError(camera: CameraDevice, error: Int) {
            tryToStopPreview()
        }
    }
    private var captureSession: CameraCaptureSession? = null
    private val captureSessionCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            this@ScanFragment.captureSession = session
            session.setRepeatingRequest(captureRequestBuilder!!.build(), null, null)
        }
        override fun onConfigureFailed(session: CameraCaptureSession) {
            tryToStopPreview()
        }
    }
    private var captureRequestBuilder: CaptureRequest.Builder? = null

    private var imageReader: ImageReader? = null
    private val onImageAvailableListener = object : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader?) {
            val image = reader?.acquireLatestImage() ?: return
            if (decoding.compareAndSet(false, true)) {
                val buffer = image.planes[0].buffer
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                decode(data, image.width, image.height)
            }
            image.close()
        }
    }

    private lateinit var previewSize: Size

    private val prepared = AtomicBoolean(false)
    private val previewing = AtomicBoolean(false)
    private val decoding = AtomicBoolean(false)
    private var decodeJob: Job? = null

    private var scanSurface: SurfaceView? = null
    private var scanTexture: TextureView? = null

    private val rotateMatrix = Matrix().also {
        it.postRotate(90F)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layoutId = arguments?.getInt("layout_id", R.layout.scan_fragment) ?: R.layout.scan_fragment
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (val previewer = view.findViewById<View>(R.id.ScanPreviewer)) {
            is SurfaceView -> {
                scanSurface = previewer
                previewer.holder.addCallback(this)
            }
            is TextureView -> {
                scanTexture = previewer
                previewer.surfaceTextureListener = this
            }
            else -> throw IllegalStateException("ScanPreviewer not found.")
        }
        (view as ResizableLayout).strategy = object : ResizableLayout.ResizeStrategy {
            override fun resize(child: View, parentWidth: Int, parentHeight: Int, ratio: Float) {
                if (child === scanSurface || child === scanTexture || child.id == R.id.capture) {
                    ResizableLayout.DEFAULT_RESIZE_STRATEGY.resize(child, parentWidth, parentHeight, ratio)
                }
            }
        }
        cameraPermission.request()
    }

    override fun onResume() {
        super.onResume()
        tryToStartPreview()
    }

    override fun onPause() {
        super.onPause()
        tryToStopPreview()
    }

    @SuppressLint("MissingPermission")
    private fun tryToStartPreview() {
        if (cameraPermission.granted && prepared.get() && previewing.compareAndSet(false, true)) {
            val cameraManager = requireContext().getSystemService(CameraManager::class.java) ?: throw IllegalStateException("CameraManager Not Found.")
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: throw IllegalStateException("Camera not found.")
            val characteristics = cameraManager.getCameraCharacteristics(cameraId)
            previewSize = decidePreviewSize(characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) as StreamConfigurationMap)
            (view as ResizableLayout).ratio = previewSize.width.toFloat() / previewSize.height.toFloat()

            if (scanSurface != null) {
                scanSurface!!.holder.setFixedSize(previewSize.width, previewSize.height)
            } else if (scanTexture != null) {
                scanTexture!!.surfaceTexture!!.setDefaultBufferSize(previewSize.width, previewSize.height)
            }
            imageReader = ImageReader.newInstance(previewSize.width, previewSize.height, ImageFormat.JPEG, 1)
            imageReader?.setOnImageAvailableListener(onImageAvailableListener, null)

            cameraManager.openCamera(cameraId, cameraDeviceStateCallback, null)
        }
    }

    private fun tryToStopPreview() {
        if (previewing.compareAndSet(true, false)) {
            captureSession?.close()
            captureSession = null
            camera?.close()
            camera = null
            captureRequestBuilder = null
            imageReader?.setOnImageAvailableListener(null, null)
            imageReader?.close()
            imageReader = null
        }
    }

    private fun decidePreviewSize(map: StreamConfigurationMap): Size {
        val targetRange = 1280 * 720 .. 1920 * 1080
        val sizes = map.getOutputSizes(ImageFormat.JPEG).filter { it.width * it.height in targetRange }.sortedBy { it.width * it.height }
//        return Size(768, 1024)
        return sizes.first().let { Size(min(it.width, it.height), max(it.width, it.height)) }.also { TAG.logV("previewsize = $it") }
    }

    private fun decode(data: ByteArray, width: Int, height: Int) {
        decodeJob?.cancel()
        decodeJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, rotateMatrix, true)
                val result = QrCodeUtil.decodeMultiple(bitmap)?.firstOrNull()

                if (result == null) {
                    decoding.compareAndSet(true, false)
                } else {
                    withContext(Dispatchers.Main) {
                        view?.findViewById<TextView>(R.id.content)?.text = result.text ?: ""
                        view?.findViewById<ImageView>(R.id.capture)?.setImageBitmap(bitmap)
                    }
                }
            } catch (e : ReaderException) {
                e.printStackTrace()
                decoding.compareAndSet(true, false)
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (prepared.compareAndSet(false, true)) {
            tryToStartPreview()
        }
    }
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (prepared.compareAndSet(true, false)) {
            tryToStopPreview()
        }
    }
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (prepared.compareAndSet(false, true)) {
            tryToStartPreview()
        }
    }
    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        if (prepared.compareAndSet(true, false)) {
            tryToStopPreview()
        }
        return true
    }
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}


    private inner class CameraPermission(
        val onChanged: (Boolean) -> Unit
    ) {

        val granted: Boolean; get() = ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        fun request(): Boolean {
            if (!granted) {
                registerForActivityResult(ActivityResultContracts.RequestPermission(), onChanged).launch(Manifest.permission.CAMERA)
                return false
            }
            return true
        }
    }
}