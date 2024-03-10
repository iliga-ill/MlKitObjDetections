package com.example.mlkit_detection

import com.example.ejisho.fragments.DTO.Icon
import com.example.ejisho.fragments.second_layer_fragment.buttonbar.ButtonbarSettings
import com.example.ejisho.pages.PageController
import com.example.mlkit_detection.pages.pages.Page_FaceRecognition
import com.example.mlkit_detection.pages.pages.Page_ImageRecognition
import com.example.mlkit_detection.pages.pages.Page_ObjectRecognition
import com.example.mlkit_detection.pages.pages.Page_TextRecognition
import com.example.mlkit_objdetections.MainActivity
import com.example.mlkit_objdetections.R

object FragmentSettings {

    fun getPageBottombarSettings(main: MainActivity, appSettings: AppSettings, pageController: PageController): ButtonbarSettings {
        return ButtonbarSettings(
            iconArr = arrayListOf(
                Icon(0, "", R.drawable.ic_user, widthDp = 40f, heightDp = 40f),
                Icon(1, "", R.drawable.ic_edit, widthDp = 40f, heightDp = 40f),
                Icon(2, "", R.drawable.ic_obj, widthDp = 40f, heightDp = 40f),
                Icon(3, "", R.drawable.ic_pen, widthDp = 40f, heightDp = 40f),
            ),
            heightDp = 60f,
            widthDp = AppMethods.pxToDp(appSettings.screenWidthPx),
            appearingAnimation = true,
            rippleAnimation = false,
            onClickAction = { iconId -> when (iconId) {
                0 -> pageController.openNewPageLayout(main, Page_FaceRecognition(appSettings.page_face_recognition_id, appSettings, pageController))
                1 -> pageController.openNewPageLayout(main, Page_ImageRecognition(appSettings.page_image_recognition_id, appSettings, pageController))
                2 -> pageController.openNewPageLayout(main, Page_ObjectRecognition(appSettings.page_object_recognition_id, appSettings, pageController))
                3 -> pageController.openNewPageLayout(main, Page_TextRecognition(appSettings.page_text_recognition_id, appSettings, pageController))
            }}
        )
    }
}