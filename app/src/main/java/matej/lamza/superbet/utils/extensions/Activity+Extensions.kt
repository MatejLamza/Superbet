package matej.lamza.superbet.utils.extensions

import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun Activity.infoSnackBar(
    view: View, message: String,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT
) = Snackbar.make(this, view, message, duration).info()

fun Activity.errorSnackBar(
    view: View, message: String,
    @BaseTransientBottomBar.Duration duration: Int = Snackbar.LENGTH_SHORT
) = Snackbar.make(this, view, message, duration).error()
