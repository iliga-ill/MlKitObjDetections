package com.example.mlkit_detection.pages.pages

import android.content.Context
import android.graphics.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.ejisho.fragments.second_layer_fragment.buttonbar.ButtonbarFragment
import com.example.ejisho.pages.Page
import com.example.ejisho.pages.PageController
import com.example.mlkit_detection.*
import com.example.mlkit_objdetections.MainActivity
import com.example.mlkit_objdetections.R
import com.google.mlkit.nl.entityextraction.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.io.IOException

class Page_TextRecognition(
    override var pageId: Long,
    override var appSettings: AppSettings,
    override var pageController: PageController,
): Page {

    private lateinit var bottomBarFragment: ButtonbarFragment

    val entityExtractor = EntityExtraction.getClient(EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH).build())

    var extractorAvailable: Boolean = false

    lateinit var txtInput: EditText
    lateinit var txtOutput: TextView
    lateinit var btnExtract: Button

    override fun openLayout(main: MainActivity) {
        println("Открыта страница разпознавания лица")

        main.layoutInflater.inflate(R.layout.text_recognition, main.getDynamicBody(), true)

        txtInput = main.findViewById(R.id.btnExtract)
        txtOutput = main.findViewById(R.id.textOutput)
        btnExtract = main.findViewById(R.id.button2)

        prepareExtractor()

        btnExtract.setOnClickListener {
            doExtraction()
        }

        //set bottombar
        main.getDynamicBottombar().removeAllViews()
        ButtonbarFragment(appSettings, FragmentSettings.getPageBottombarSettings(main, appSettings, pageController))
            .also { bottomBarFragment = it }
            .createView(main.layoutInflater, main.getDynamicBottombar())

        bottomBarFragment.selectButton(arrayListOf(3))

        //добавление страницы в стак
        pageController.clearPageArray()
    }

    fun prepareExtractor(){
        entityExtractor.downloadModelIfNeeded().addOnSuccessListener {
            extractorAvailable = true
        }
        .addOnFailureListener{
            extractorAvailable = false
        }
    }

    fun doExtraction(){
        if (extractorAvailable) {
            val userText = txtInput.text.toString()
            val params = EntityExtractionParams.Builder(userText).build()
            var outputString = ""
            entityExtractor.annotate(params)
                .addOnSuccessListener { result: List<EntityAnnotation> ->
                    for (entityAnnotation in result) {
                        outputString += entityAnnotation.annotatedText
                        for (entity in entityAnnotation.entities) {
                            outputString += " : " + getStringFor(entity)
                        }
                        outputString += "\n\n"
                    }
                    txtOutput.text = outputString
                }
                .addOnFailureListener{

                }
        }
    }

    fun getStringFor(entity: Entity): String {
        var returnVal = "Type - "
        when(entity.type) {
            Entity.TYPE_ADDRESS -> returnVal += "Address"
            Entity.TYPE_DATE_TIME -> returnVal += "DateTime"
            Entity.TYPE_EMAIL -> returnVal += "Email Address"
            Entity.TYPE_FLIGHT_NUMBER -> returnVal += "Flight Number"
            Entity.TYPE_IBAN -> returnVal += "IBAN"
            Entity.TYPE_ISBN -> returnVal += "ISBN"
            Entity.TYPE_MONEY -> returnVal += "Money"
            Entity.TYPE_PAYMENT_CARD -> returnVal += "Credit/Debit Card"
            Entity.TYPE_PHONE -> returnVal += "Phone Number"
            Entity.TYPE_TRACKING_NUMBER -> returnVal += "Tracking Number"
            Entity.TYPE_URL -> returnVal += "URL"
            else -> returnVal += "Address"
        }
        return returnVal
    }



}