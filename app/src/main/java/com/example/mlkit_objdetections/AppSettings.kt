package com.example.mlkit_detection

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.example.mlkit_objdetections.MainActivity

class AppSettings(
    private val main: MainActivity,
    private val settingsPreference: SharedPreferences
    ) {
    private val displayMetrics = DisplayMetrics()

    val context: Context = main
    val resources: Resources = main.resources
    val layoutInflater: LayoutInflater = main.layoutInflater
    val supportFragmentManager: FragmentManager = main.supportFragmentManager
    val lifecycle: Lifecycle = main.lifecycle

    init {
        //получение характеристик экрана
        main.windowManager.defaultDisplay.getMetrics(displayMetrics)
    }

    //objects id
    val page_face_recognition_id: Long = 1
    val page_image_recognition_id: Long = 2
    val page_object_recognition_id: Long = 3
    val page_text_recognition_id: Long = 4

    //blocks height
    val screenWidthPx = displayMetrics.widthPixels
    val screenHeightPx = displayMetrics.heightPixels
    val headerBarHeightDp = 50f //main.getHeaderBar()
    val bottombarHeightDp = 60f //main.getBottombarLayout()
    val getAndroidNavigationBarHeightPx = main.resources.getDimensionPixelSize(main.resources.getIdentifier("navigation_bar_height", "dimen", "android"))
    val getAndroidStatusBarHeightPx = main.resources.getDimensionPixelSize(main.resources.getIdentifier("status_bar_height", "dimen", "android"))
    val dynamicBodyHeightPx = screenHeightPx - AppMethods.dpToPx(headerBarHeightDp) - AppMethods.dpToPx(bottombarHeightDp) - getAndroidNavigationBarHeightPx - getAndroidStatusBarHeightPx - 10

}