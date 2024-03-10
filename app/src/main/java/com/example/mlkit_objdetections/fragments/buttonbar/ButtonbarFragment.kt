package com.example.ejisho.fragments.second_layer_fragment.buttonbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.ejisho.fragments.DTO.Icon
import com.example.ejisho.fragments.first_layer_fragments.icons.IconFragment
import com.example.ejisho.fragments.first_layer_fragments.icons.IconSettings
import com.example.mlkit_detection.AppMethods
import com.example.mlkit_detection.AppSettings
import com.example.mlkit_detection.fragments.CoroutineFragment
import com.example.mlkit_detection.fragments.CoroutineFragmentSettings
import com.example.mlkit_objdetections.R

data class ButtonbarSettings(
    val iconArr: ArrayList<Icon>,
    val widthDp: Float,
    val heightDp: Float,

    val horizontalMarginDp: Float = 0f,

    val appearingAnimation: Boolean = false,
    val rippleAnimation: Boolean = true,

    val onClickAction: ((iconId: Int)->Unit)? = null,

    override var bundle: Bundle? = null
): CoroutineFragmentSettings

class ButtonbarFragment(
    override var appSettings: AppSettings,
    override var settings: ButtonbarSettings,
) : Fragment(), CoroutineFragment<ButtonbarSettings> {
    override var bundle = settings.bundle
    override var isAsync = false

    //variables
    private var iconFragmentsArr: ArrayList<IconFragment> = arrayListOf()

    //views
    override lateinit var fragmentView: View
    private lateinit var barContainer: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentView = inflater.inflate(R.layout.fragment_empty_horizontal_container, container, container != null)
        barContainer = fragmentView.findViewById(R.id.body)

        build(inflater, container)

        return fragmentView
    }

    override fun buildFragment(inflater: LayoutInflater, container: ViewGroup?) {
        barContainer.layoutParams.width = AppMethods.dpToPx(settings.widthDp)
        barContainer.layoutParams.height = AppMethods.dpToPx(settings.heightDp)

        settings.iconArr.forEach { icon ->
            val iconContainerView = inflater.inflate(R.layout.fragment_empty_horizontal_container, null, false)
            val iconContainerBody = iconContainerView.findViewById<LinearLayout>(R.id.body)

            IconFragment(appSettings, IconSettings(
                iconArr = arrayListOf(icon),
                initialIconId = icon.id,
                containerHeightDp = settings.heightDp,
                containerWidthDp = settings.widthDp/settings.iconArr.size,
                appearingAnimation = settings.appearingAnimation,
                rippleAnimation = settings.rippleAnimation,
                onClickAction = { settings.onClickAction?.invoke(it) }
            )).also { iconFragmentsArr.add(it) }.createView(inflater, iconContainerBody)


            barContainer.addView(iconContainerView)
        }
    }

    fun selectButton(iconsId: ArrayList<Int>, instantSelect: Boolean = false){
        iconFragmentsArr.forEach { iconFragment ->
            if (iconsId.contains(iconFragment.getCurrentIconId())){
                if (!instantSelect) iconFragment.highlightIcon(true)
                else iconFragment.highlightIcon(false)
            } else iconFragment.dimIcon(false)
        }
    }

    fun disableButton(iconId: Int, useAppearingAnimation: Boolean = false){
        iconFragmentsArr.first { it.getCurrentIconId() == iconId }.disableIcon(useAppearingAnimation)
    }

    fun enableButton(iconId: Int, useAppearingAnimation: Boolean = false){
        iconFragmentsArr.first { it.getCurrentIconId() == iconId }.enableIcon(useAppearingAnimation)
    }

    fun onIconClick(iconId: Int, action: (index: Int)-> Unit){
        iconFragmentsArr.first {it.getCurrentIconId() == iconId}.onClickListener(action)
    }

}