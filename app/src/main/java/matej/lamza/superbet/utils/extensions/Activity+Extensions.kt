package matej.lamza.superbet.utils.extensions

import android.app.Activity
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.WindowMetrics


fun Activity.getScreenMeasurements(): ScreenDimensions {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val metrics: WindowMetrics = this.getSystemService(WindowManager::class.java).currentWindowMetrics
        with(metrics.bounds) { return@with ScreenDimensions(width(), height()) }
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        ScreenDimensions(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }
}
