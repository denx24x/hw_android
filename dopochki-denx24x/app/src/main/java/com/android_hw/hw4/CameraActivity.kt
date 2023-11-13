package com.android_hw.hw4

import CameraHandler
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.camera.core.*
import androidx.camera.view.PreviewView
import com.google.android.material.button.MaterialButton
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class CameraActivity : AppCompatActivity() {
    private val TAG = "CAMERA"
    private lateinit var previewView : PreviewView



    private val cameraHandler by lazy {
        CameraHandler(
            caller = this,
            previewView = previewView,
            onPictureTaken = { file, uri ->
                sendPicture()
            },
            builderPreview = Preview.Builder().setTargetResolution(Size(800, 800)),
            builderImageCapture = ImageCapture.Builder().setTargetResolution(Size(800, 800))
        )
    }

    fun sendPicture(){
        val mApp = (application as MyApp)
         val file = File(application.cacheDir, "cameraCapture.jpg")
            val inputStream = file.inputStream()
            val filePart = MultipartBody.Part.createFormData(
                "pic",
                "${System.currentTimeMillis()}-cameraCapture.jpg",
                RequestBody.create(
                    "img/jpeg".toMediaType(),
                    inputStream.readBytes()
                )
            )
            inputStream.close()

            val messagePart =
                "{\"from\": \"${USERNAME}\", \"to\": \"${mApp.currentChannel}\"}"
                    .toRequestBody("application/json".toMediaType())


            mApp.service.sendImage(
                messagePart, filePart
            ).enqueue(SendCallback(applicationContext))
            finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera)
        val buttonCapture = findViewById<MaterialButton>(R.id.button_capture)
        val buttonSwitch = findViewById<MaterialButton>(R.id.button_switch)
        val switchFlash = findViewById<SwitchCompat>(R.id.flash)
        previewView = findViewById(R.id.previewView)

        buttonSwitch.setOnClickListener { cameraHandler.changeCamera() }
        buttonCapture.setOnClickListener { cameraHandler.takePicture() }

        switchFlash.setOnCheckedChangeListener{ _, value: Boolean ->
            if(value){
                cameraHandler.imageCapture.flashMode = ImageCapture.FLASH_MODE_ON
            }else{
                cameraHandler.imageCapture.flashMode = ImageCapture.FLASH_MODE_OFF
            }
        }
        previewView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if(event == null) return false
                if (event.action == MotionEvent.ACTION_DOWN) {
                    return true
                }else if (event.action == MotionEvent.ACTION_UP){
                    if(cameraHandler.camera == null) true
                    val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                        previewView.width.toFloat(), previewView.height.toFloat()
                    )
                    val focusPoint = factory.createPoint(event.x, event.y)
                    cameraHandler.camera!!.cameraControl.startFocusAndMetering(
                        FocusMeteringAction.Builder(
                            focusPoint,
                            FocusMeteringAction.FLAG_AF
                        ).apply {
                            disableAutoCancel()
                        }.build()
                    )
                    return true
                }else{
                    return false
                }
            }

        })

        cameraHandler.start()
    }

    override fun onStop() {
        super.onStop()
        cameraHandler.cameraProvider?.unbindAll()
    }
}