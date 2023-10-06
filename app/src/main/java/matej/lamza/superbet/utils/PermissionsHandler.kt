package matej.lamza.superbet.utils

import android.Manifest
import android.app.Activity
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import matej.lamza.superbet.R
import matej.lamza.superbet.utils.extensions.infoSnackBar


private const val TAG = "PermissionHandler"

object PermissionsHandler {

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun requestPermission(
        activity: Activity,
        view: View,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        when {
            shouldShowRationale(activity) -> showLocationRationaleInfo(activity, view, launcher)

            else -> launcher.launch(PERMISSIONS)
        }
    }

    private fun showLocationRationaleInfo(
        activity: Activity,
        view: View,
        launcher: ActivityResultLauncher<Array<String>>
    ) {
        activity.infoSnackBar(view, activity.getString(R.string.permission_rationale), Snackbar.LENGTH_LONG)
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    launcher.launch(PERMISSIONS)
                }
            })
            .show()
    }

    private fun shouldShowRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}
