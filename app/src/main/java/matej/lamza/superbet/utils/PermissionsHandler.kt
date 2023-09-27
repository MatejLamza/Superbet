package matej.lamza.superbet.utils

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


private const val TAG = "PermissionHandler"

object PermissionsHandler {

    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun requestPermission(context: Context, activity: Activity, launcher: ActivityResultLauncher<Array<String>>) {
        when {
            //check if alredy have permissions
            shouldShowRationale(activity) -> showLocationRationaleDialog(context) { launcher.launch(PERMISSIONS) }
            else -> launcher.launch(PERMISSIONS)
        }
    }

    //todo extract to string resources, maybe more customization ?
    private fun showLocationRationaleDialog(context: Context, askForLocationPermission: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Location Permission Needed!")
            .setMessage("In order to see bet shops near you, please grant location permissions.")
            .setPositiveButton("OK") { dialogInterface, _ -> askForLocationPermission() }
            .setNegativeButton("CANCEL") { dialogInterface, _ -> dialogInterface.dismiss() }
            .create()
            .show()
    }

    fun checkIfPermissionsAreAlreadyGranted(context: Context): Boolean {
        return PERMISSIONS.map { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }.none { isGranted -> !isGranted }
    }

    private fun shouldShowRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
    }
}
