package com.example.ejisho.pages

import com.example.mlkit_detection.AppSettings
import com.example.mlkit_objdetections.MainActivity

interface Page {

    var pageId: Long
    var appSettings: AppSettings
    var pageController: PageController

    fun openLayout(main: MainActivity)

    fun onPageClose(main: MainActivity, isConfirmed: Boolean): Boolean { return true }

}