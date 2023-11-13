
import android.app.Activity
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val RATIO_4_3_VALUE = 4.0 / 3.0
private const val RATIO_16_9_VALUE = 16.0 / 9.0

class CameraHandler (
    private val caller: AppCompatActivity,
    private val previewView: PreviewView,
    private val imageAnalizer: ImageAnalysis.Analyzer? = null,
    private val onPictureTaken: ((File, Uri?) -> Unit)? = null,
    private val builderPreview: Preview.Builder? = null,
    private val builderImageCapture: ImageCapture.Builder? = null
) {
    private val TAG = "CAMERA_HANDLER"

    private lateinit var imagePreview: Preview
    lateinit var imageCapture: ImageCapture
    private var imageAnalysis: ImageAnalysis? = null

    private var lensFacing = CameraSelector.LENS_FACING_FRONT
    private val executor = Executors.newSingleThreadExecutor()
    var cameraProvider: ProcessCameraProvider? = null
    var camera : Camera? = null

    fun start() {
        previewView.post { startCamera() }
    }


    private fun createImagePreview() =
        (builderPreview ?: Preview.Builder()
            .setTargetAspectRatio(aspectRatio()))
            .setTargetRotation(previewView.display.rotation)
            .build()
            .apply { setSurfaceProvider(previewView.previewSurfaceProvider) }

    private fun createImageAnalysis() =
        ImageAnalysis.Builder()
            .setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply { imageAnalizer?.let { setAnalyzer(executor, imageAnalizer) } }

    private fun createImageCapture() =
        (builderImageCapture ?: ImageCapture.Builder()
            .setTargetAspectRatio(aspectRatio()))
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .build()

    fun changeCamera() {
        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_FRONT){
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }

        startCamera()
    }

    private fun startCamera() {
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(caller)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            imagePreview = createImagePreview()
            imagePreview.setSurfaceProvider(previewView.previewSurfaceProvider)

            imageCapture = createImageCapture()
            imageAnalysis = createImageAnalysis()

            cameraProvider!!.unbindAll()
            camera = cameraProvider!!.bindToLifecycle(
                caller as LifecycleOwner,
                cameraSelector,
                imagePreview,
                imageCapture,
                imageAnalysis
            )

        }, ContextCompat.getMainExecutor(caller))
    }

    private fun aspectRatio(): Int {
        val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    fun takePicture() {
        val dir = caller.cacheDir

        val file = File(dir, "cameraCapture.jpg")
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture.takePicture(
            outputFileOptions,
            executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onPictureTaken?.invoke(
                        file,
                        outputFileResults.savedUri
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.i(TAG, exception.toString())
                }
            })
    }
}