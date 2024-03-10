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
import java.io.IOException

class Page_ImageRecognition(
    override var pageId: Long,
    override var appSettings: AppSettings,
    override var pageController: PageController,
): Page {

    private lateinit var bottomBarFragment: ButtonbarFragment


    override fun openLayout(main: MainActivity) {
        println("Открыта страница разпознавания лица")

        main.layoutInflater.inflate(R.layout.image_recognition, main.getDynamicBody(), true)

        val img: ImageView = main.findViewById(R.id.imageCat)
        val fileName = "cat.jpg"

        val bitmap: Bitmap? = main.assetsToBitmap(fileName)
        bitmap?.apply{
            img.setImageBitmap(this)
        }

        //setup classification stuff
        val txtOutput: TextView = main.findViewById(R.id.txtOutput)
        val btn: Button = main.findViewById(R.id.btnTest)

        btn.setOnClickListener {
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap!!, 0)
            var outputText = ""
            labeler.process(image).addOnSuccessListener { lables ->
                for (label in lables){
                    val text = label.text
                    val confidence = label.confidence
                    outputText += "$text : $confidence\n"
                }
                txtOutput.text = outputText
            }
            .addOnFailureListener{ e ->

            }
        }

        //set bottombar
        main.getDynamicBottombar().removeAllViews()
        ButtonbarFragment(appSettings, FragmentSettings.getPageBottombarSettings(main, appSettings, pageController))
            .also { bottomBarFragment = it }
            .createView(main.layoutInflater, main.getDynamicBottombar())

        bottomBarFragment.selectButton(arrayListOf(1))

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


}