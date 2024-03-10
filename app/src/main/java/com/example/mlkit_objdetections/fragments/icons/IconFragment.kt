package com.example.ejisho.fragments.first_layer_fragments.icons

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.ejisho.fragments.DTO.Icon
import com.example.mlkit_detection.AppMethods
import com.example.mlkit_detection.AppSettings
import com.example.mlkit_detection.fragments.CoroutineFragment
import com.example.mlkit_detection.fragments.CoroutineFragmentSettings
import com.example.mlkit_objdetections.R
import com.google.android.material.color.MaterialColors

data class IconSettings(
    val iconArr: ArrayList<Icon>,
    val initialIconId: Int,
    val containerHeightDp: Float,
    val containerWidthDp: Float,
    val icon_ml: Float = 0f,
    val icon_mt: Float = 0f,
    val icon_mr: Float = 0f,
    val icon_mb: Float = 0f,
    val container_ml: Float = 0f,
    val container_mt: Float = 0f,
    val container_mr: Float = 0f,
    val container_mb: Float = 0f,
    val appearingAnimation: Boolean = false,
    val rippleAnimation: Boolean = true,
    var onClickAction: ((index: Int)-> Unit)? = null,

    override var bundle: Bundle? = null
): CoroutineFragmentSettings

class IconFragment(
    override var appSettings: AppSettings,
    override var settings: IconSettings
): Fragment(), CoroutineFragment<IconSettings> {
    override var bundle = settings.bundle
    override var isAsync = false

    //variables
    private var activeIcon: Icon = settings.iconArr.first { it.id == settings.initialIconId }
    private var isHighlighted = false

    //views
    override lateinit var fragmentView: View
    private lateinit var containerLayout: CardView
    private lateinit var imageView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentView = inflater.inflate(R.layout.fragment_icon_container, container, container != null)
        containerLayout = fragmentView.findViewById(R.id.container_layout)
        imageView = fragmentView.findViewById(R.id.image_view)

        build(inflater, container)

        return fragmentView
    }

    override fun buildFragment(inflater: LayoutInflater, container: ViewGroup?) {
        adjustIcon()

        setIconToView(activeIcon)
    }

    private fun adjustIcon(){
        containerLayout.layoutParams.height = AppMethods.dpToPx(settings.containerHeightDp)
        containerLayout.layoutParams.width = AppMethods.dpToPx(settings.containerWidthDp)

        if (settings.icon_ml!=0f || settings.icon_mt!=0f || settings.icon_mr!=0f || settings.icon_mb!=0f) AppMethods.setMarginToView(imageView, settings.icon_ml, settings.icon_mt, settings.icon_mr, settings.icon_mb)
        if (settings.container_ml!=0f || settings.container_mt!=0f || settings.container_mr!=0f || settings.container_mb!=0f) AppMethods.setMarginToView(containerLayout, settings.container_ml, settings.container_mt, settings.container_mr, settings.container_mb)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setIconToView(icon: Icon) {
        imageView.layoutParams.height = AppMethods.dpToPx(icon.heightDp)
        imageView.layoutParams.width = AppMethods.dpToPx(icon.widthDp)

        imageView.background = appSettings.context.getDrawable(icon.iconImageId)

        activeIcon = icon
        enableIcon()
    }

    @SuppressLint("ResourceType")
    fun dimIcon(useAppearingAnimation: Boolean) {
        if (useAppearingAnimation && settings.appearingAnimation && isHighlighted) {
            AppMethods.blendColors(
                MaterialColors.getColor(appSettings.context, activeIcon.enabledIconColorId, Color.BLACK),
                MaterialColors.getColor(appSettings.context, activeIcon.disabledIconColorId, Color.BLACK),
                300L
            ) { color ->
                imageView.backgroundTintList = AppMethods.colorStateListWithoutContext(color)
            }
            isHighlighted = false
        } else {
            containerLayout.foreground = null
            val attrStateList = AppMethods.attrColorStateListFrom(appSettings.context, activeIcon.disabledIconColorId)
            if (attrStateList.defaultColor != 0) imageView.backgroundTintList = attrStateList
            else imageView.backgroundTintList = AppMethods.colorStateListWithContext(appSettings.context, activeIcon.disabledIconColorId)
        }
    }
    @SuppressLint("ResourceType")
    fun highlightIcon(useAppearingAnimation: Boolean) {
        if (useAppearingAnimation && settings.appearingAnimation && !isHighlighted) {
            AppMethods.blendColors(
                MaterialColors.getColor(appSettings.context, activeIcon.disabledIconColorId, Color.BLACK),
                MaterialColors.getColor(appSettings.context, activeIcon.enabledIconColorId, Color.BLACK),
                300L
            ) { color ->
                imageView.backgroundTintList = AppMethods.colorStateListWithoutContext(color)
            }
            isHighlighted = true
        } else {
            containerLayout.foreground = appSettings.context.getDrawable(R.drawable.bg_ripple_animation_on_click)
            val attrStateList = AppMethods.attrColorStateListFrom(appSettings.context, activeIcon.enabledIconColorId)
            if (attrStateList.defaultColor != 0) imageView.backgroundTintList = attrStateList
            else imageView.backgroundTintList = AppMethods.colorStateListWithContext(appSettings.context, activeIcon.enabledIconColorId)
        }
    }

    fun disableIcon(useAppearingAnimation: Boolean = false) {
        dimIcon(useAppearingAnimation)
        containerLayout.setOnClickListener {}
    }

    fun enableIcon(useAppearingAnimation: Boolean = false) {
        highlightIcon(useAppearingAnimation)
        if (settings.onClickAction != null) onClickListener(settings.onClickAction!!)
    }

    fun onClickListener(action: (index: Int)-> Unit){
        containerLayout.setOnClickListener {
            if (settings.iconArr.size>1){
                var nextIconIndex = 0
                for (i in 0..settings.iconArr.size-1) if (settings.iconArr[i].id == activeIcon.id) nextIconIndex = i+1

                if (nextIconIndex < settings.iconArr.size) setIconToView(settings.iconArr[nextIconIndex])
                else if (nextIconIndex == settings.iconArr.size) setIconToView(settings.iconArr[0])
            }
            action.invoke(getCurrentIconId())
        }
        settings.onClickAction = action
    }

    fun setIconById(id: Int) {setIconToView(settings.iconArr.first { it.id == id } )}
    fun getCurrentIconId(): Int {return activeIcon.id}
    fun getWidth(): Int {
        AppMethods.measureView(fragmentView)
        return fragmentView.measuredWidth
    }
}
