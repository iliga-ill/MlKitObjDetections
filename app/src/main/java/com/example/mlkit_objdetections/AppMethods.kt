package com.example.mlkit_detection

import android.Manifest
import android.animation.ValueAnimator
import android.app.*
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.*
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.doOnDetach
import com.example.mlkit_objdetections.MainActivity

object AppMethods {
    //helpers
    fun dpToPx(dp: Float): Int{ return (dp * (Resources.getSystem().getDisplayMetrics().densityDpi / 160f)).toInt() }
    fun dpToPx(dp: Int): Int{ return (dp.toFloat() * (Resources.getSystem().getDisplayMetrics().densityDpi / 160f)).toInt() }
    fun pxToDp(px: Float): Float{ return (px * 160f) / Resources.getSystem().getDisplayMetrics().densityDpi }
    fun pxToDp(px: Int): Float{ return (px * 160f) / Resources.getSystem().getDisplayMetrics().densityDpi }

    fun setFocusOnInput(main: MainActivity, input: EditText){
        input.requestFocus()
        val imm = main.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS)

        input.doOnDetach { clearFocusOnInput(main, input) } //?????
    }

    fun clearFocusOnInput(main: MainActivity, input: EditText){
        val imm: InputMethodManager = main.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
        input.clearFocus()
    }

    //<editor-fold desc="Colors">
    fun colorStateListWithoutContext(color: Int): ColorStateList{
        return ColorStateList(arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled), // disabled
            intArrayOf(android.R.attr.state_checked), // unchecked
            intArrayOf(-android.R.attr.state_checked), // unchecked
            intArrayOf(android.R.attr.state_pressed)  // pressed
        ), intArrayOf(
            color, 
            color, 
            color,
            color, 
            color
        ))
    }

    fun colorStateListWithContext(context: Context, color: Int): ColorStateList{
        return ColorStateList.valueOf(context.getColor(color))
    }

    fun attrColor(context: Context, attrColorId: Int): Int{
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrColorId, typedValue, true)
        return typedValue.data
    }

    fun attrColorStateListFrom(context: Context, attrColorId: Int): ColorStateList {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrColorId, typedValue, true)
        return ColorStateList.valueOf(typedValue.data)
    }

    fun blendColors(colorFrom: Int, colorTo: Int, animDuration: Long, tickAction: (color: Int) -> Unit) { //returns color without context
        val anim: ValueAnimator = ValueAnimator.ofFloat(0F, 1F)
        anim.addUpdateListener { animation -> // Use animation position to blend colors.
            val position: Float = animation.animatedFraction

            // Apply blended color to the status bar.
            val blended = blendColors(colorFrom, colorTo, position)
            tickAction.invoke(blended)
        }
        anim.setDuration(animDuration).start()
    }

    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
        val inverseRatio = 1f - ratio
        val r: Float = Color.red(to) * ratio + Color.red(from) * inverseRatio
        val g: Float = Color.green(to) * ratio + Color.green(from) * inverseRatio
        val b: Float = Color.blue(to) * ratio + Color.blue(from) * inverseRatio
        return Color.rgb(r.toInt(), g.toInt(), b.toInt())
    }
    //</editor-fold>

    fun measureView(view: View){
        val wrapSpec = View.MeasureSpec.makeMeasureSpec(1, View.MeasureSpec.UNSPECIFIED)
        view.measure(wrapSpec, wrapSpec)
    }

    fun equalBundles(one: Bundle?, two: Bundle?): Boolean {
        if (one != null && two != null) {
            if (one.size() !== two.size()) return false
            val setOne: MutableSet<String> = HashSet(one.keySet())
            setOne.addAll(two.keySet())
            var valueOne: Any
            var valueTwo: Any
            for (key in setOne) {
                if (!one.containsKey(key) || !two.containsKey(key)) return false
                valueOne = one.get(key)!!
                valueTwo = two.get(key)!!
                if (valueOne is Bundle && valueTwo is Bundle && !equalBundles(valueOne, valueTwo)) return false
                else if (valueOne == null && valueTwo != null) return false
                else if (valueOne != valueTwo) return false
            }
        }
        return true
    }

    fun setMarginToView(view: View, mlDp: Float, mtDp: Float, mrDp: Float, mbDp: Float){
        val viewMarginParams = view.getLayoutParams() as ViewGroup.MarginLayoutParams
        viewMarginParams.setMargins(
            dpToPx(mlDp),
            dpToPx(mtDp),
            dpToPx(mrDp),
            dpToPx(mbDp)
        )
    }

    fun delay(clickDelay:Long = 100, action: ()-> Unit){
        object : CountDownTimer(clickDelay, clickDelay) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {action.invoke()}
        }.start()
    }

    fun timer(tickTime:Long, finishTime: Long, onTickAction: ((timeFromStart: Long)-> Unit)? = null, onFinishAction: (()-> Unit)? = null){
        object : CountDownTimer(finishTime, tickTime) {
            override fun onTick(p0: Long) {onTickAction?.invoke(p0)}
            override fun onFinish() {onFinishAction?.invoke()}
        }.start()
    }

    fun showToastNotification(main: MainActivity, msg: String){
        Handler(Looper.getMainLooper()).post( kotlinx.coroutines.Runnable {
            Toast.makeText(main, msg, Toast.LENGTH_LONG).show()
        })
    }

    fun getSensors(main: MainActivity): List<Sensor> {
        val sensorManager = main.getSystemService(SENSOR_SERVICE) as SensorManager
        return sensorManager.getSensorList(Sensor.TYPE_ALL)
    }

    fun checkSensor(main: MainActivity, sensorTypeId: Int): Boolean { //Sensor.TYPE_GYROSCOPE, Sensor.TYPE_ACCELEROMETER and so on
        val sensorManager = main.getSystemService(SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(sensorTypeId) != null) return true
        return false
    }

    fun checkPermission(main: MainActivity) {
        val requestPermissionLauncher = main.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {} // Permission is granted. Continue the action or workflow in your app
            else {} // Explain to the user that the feature is unavailable because the feature requires a permission that the user has denied
        }
        requestPermissionLauncher.launch(Manifest.permission.CHANGE_NETWORK_STATE)
        requestPermissionLauncher.launch(Manifest.permission.INTERNET)
        requestPermissionLauncher.launch(Manifest.permission.WRITE_CALENDAR)
    }
}