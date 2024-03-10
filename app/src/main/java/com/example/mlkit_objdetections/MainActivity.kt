package com.example.mlkit_objdetections

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.example.ejisho.pages.PageController
import com.example.mlkit_detection.AppSettings

class MainActivity : AppCompatActivity() {

    //controllers
    private lateinit var appSettings: AppSettings
    private lateinit var pageController: PageController

    override fun onCreate(savedInstanceState: Bundle?) {
        //инициализация настроек приложения
        appSettings = AppSettings(this, this.getSharedPreferences("SETTINGS", MODE_PRIVATE))

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // инициализация контроллера страниц, нужны настройки и BottomBar котроллер
        PageController(appSettings).also { pageController = it }.connect(this)
    }


    fun getDynamicBody(): LinearLayout = this.findViewById(R.id.dynamic_body)
    fun getDynamicBottombar(): LinearLayout = this.findViewById(R.id.dynamic_bottombar)
}