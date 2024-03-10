package com.example.mlkit_detection.pages.pages

import android.content.Context
import android.graphics.*
import android.widget.Button
import android.widget.ImageView
import com.example.ejisho.fragments.second_layer_fragment.buttonbar.ButtonbarFragment
import com.example.ejisho.pages.Page
import com.example.ejisho.pages.PageController
import com.example.mlkit_detection.*
import com.example.mlkit_objdetections.MainActivity
import com.example.mlkit_objdetections.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.io.IOException

class Page_FaceRecognition(
    override var pageId: Long,
    override var appSettings: AppSettings,
    override var pageController: PageController,
): Page {

    private lateinit var bottomBarFragment: ButtonbarFragment


    override fun openLayout(main: MainActivity) {
        println("Открыта страница разпознавания лица")

        main.layoutInflater.inflate(R.layout.face_recognition, main.getDynamicBody(), true)

        val img: ImageView = main.findViewById(R.id.imageFace)
        val fileName = "face2.jpg"

        val bitmap: Bitmap? = main.assetsToBitmap(fileName)
        bitmap?.apply{
            img.setImageBitmap(this)
        }

        val btn: Button = main.findViewById(R.id.btnTest)
        btn.setOnClickListener{
            val highAccuracyOpts = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()

            val detector = FaceDetection.getClient(highAccuracyOpts)
            val image = InputImage.fromBitmap(bitmap!!, 0)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    bitmap?.apply {
                        img.setImageBitmap(drawWithRectangle(faces))
                    }
                }
                .addOnFailureListener{e ->

                }
        }

        //set bottombar
        main.getDynamicBottombar().removeAllViews()
        ButtonbarFragment(appSettings, FragmentSettings.getPageBottombarSettings(main, appSettings, pageController))
            .also { bottomBarFragment = it }
            .createView(main.layoutInflater, main.getDynamicBottombar())

        bottomBarFragment.selectButton(arrayListOf(0))

        //добавление страницы в стак
        pageController.clearPageArray()
    }


    fun Context.assetsToBitmap(fileName: String): Bitmap?{
        return try {
            with(assets.open(fileName)){
                BitmapFactory.decodeStream(this)
            }
        } catch (e: IOException) { null }
    }

    fun Bitmap.drawWithRectangle(faces: List<Face>): Bitmap?{
        val bitmap = copy(config, true)
        val canvas = Canvas(bitmap)
        for (face in faces) {
            val bounds = face.boundingBox
            Paint().apply {
                color = Color.RED
                style = Paint.Style.STROKE
                strokeWidth = 4.0f
                isAntiAlias = true
                canvas.drawRect(bounds, this)
            }
        }
        return bitmap
    }


}