package com.example.mlkit_detection.pages.pages

import android.content.Context
import android.graphics.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.ejisho.fragments.second_layer_fragment.buttonbar.ButtonbarFragment
import com.example.ejisho.pages.Page
import com.example.ejisho.pages.PageController
import com.example.mlkit_detection.*
import com.example.mlkit_objdetections.MainActivity
import com.example.mlkit_objdetections.R
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.io.IOException

class Page_ObjectRecognition(
    override var pageId: Long,
    override var appSettings: AppSettings,
    override var pageController: PageController,
): Page {

    private lateinit var bottomBarFragment: ButtonbarFragment


    override fun openLayout(main: MainActivity) {
        println("Открыта страница разпознавания лица")

        main.layoutInflater.inflate(R.layout.object_recognition, main.getDynamicBody(), true)

        val img: ImageView = main.findViewById(R.id.imageView)
        val fileName = "bird2.jpeg"

        val bitmap: Bitmap? = main.assetsToBitmap(fileName)
        bitmap?.apply{
            img.setImageBitmap(this)
        }

        //setup classification stuff
        val txtOutput: TextView = main.findViewById(R.id.textView)
        val btn: Button = main.findViewById(R.id.button)

        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .build()

        btn.setOnClickListener {
            val objectDetector = ObjectDetection.getClient(options)
            val image = InputImage.fromBitmap(bitmap!!, 0)

            objectDetector.process(image).addOnSuccessListener { detectedObjects ->
                bitmap?.apply {
                    img.setImageBitmap(drawWithRectangle(detectedObjects))
                }
                getLabels(bitmap, detectedObjects, txtOutput)
            }
            .addOnFailureListener{ e ->

            }
        }

        //set bottombar
        main.getDynamicBottombar().removeAllViews()
        ButtonbarFragment(appSettings, FragmentSettings.getPageBottombarSettings(main, appSettings, pageController))
            .also { bottomBarFragment = it }
            .createView(main.layoutInflater, main.getDynamicBottombar())

        bottomBarFragment.selectButton(arrayListOf(2))

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

    fun Bitmap.drawWithRectangle(objects: List<DetectedObject>): Bitmap?{
        val bitmap = copy(config, true)
        val canvas = Canvas(bitmap)
        var thisLabel = 0
        for (obj in objects) {
            thisLabel++
            val bounds = obj.boundingBox
            Paint().apply {
                color = Color.RED
                style = Paint.Style.STROKE
                textSize = 32f
                strokeWidth = 4f
                isAntiAlias = true
                canvas.drawRect(bounds, this)
                canvas.drawText(
                    thisLabel.toString(),
                    bounds.left.toFloat(),
                    bounds.top.toFloat(),
                    this
                )
            }
        }
        return bitmap
    }

    fun getLabels(bitmap: Bitmap, objects: List<DetectedObject>, txtOutput: TextView) {
        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
        for (obj in objects) {
            val bounds = obj.boundingBox
            val croppedBitmap = Bitmap.createBitmap(
                bitmap,
                bounds.left,
                bounds.top,
                bounds.width(),
                bounds.height()
            )
            val image = InputImage.fromBitmap(croppedBitmap, 0)

            labeler.process(image)
                .addOnSuccessListener { labels ->
                    var labelText = ""
                    if (labels.count() > 0) {
                        labelText = txtOutput.text.toString()
                        for (thisLabel in labels) {
                            labelText += thisLabel.text + " , "
                        }
                        labelText += "\n"
                    } else {
                        labelText = "Not Found." + "\n"
                    }
                    txtOutput.text = labelText.toString()
                }
        }
    }


}