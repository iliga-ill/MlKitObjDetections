package com.example.ejisho.fragments.first_layer_fragments.icons.wrap

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.mlkit_detection.AppSettings
import com.example.mlkit_detection.fragments.CoroutineFragment
import com.example.mlkit_detection.fragments.CoroutineFragmentSettings
import com.example.mlkit_objdetections.R

data class ScrollSettings(
    var contentView: View,
    var isInsideScrollView: Boolean = false,

    override var bundle: Bundle? = null
): CoroutineFragmentSettings

class ScrollFragment(
    override var appSettings: AppSettings,
    override var settings: ScrollSettings
): Fragment(), CoroutineFragment<ScrollSettings> {
    override var bundle = settings.bundle
    override var isAsync = false

    override lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (!settings.isInsideScrollView)
            fragmentView = inflater.inflate(R.layout.fragment_scroll_container, container, container != null)
        else
            fragmentView = inflater.inflate(R.layout.fragment_nested_scroll_container, container, container != null)

        build(inflater, container)

        return fragmentView
    }

    override fun buildFragment(inflater: LayoutInflater, container: ViewGroup?) {
        fragmentView.findViewById<LinearLayout>(R.id.body).addView(settings.contentView)
    }

}