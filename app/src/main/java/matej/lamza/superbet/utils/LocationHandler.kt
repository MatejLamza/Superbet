package matej.lamza.superbet.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import matej.lamza.core_model.exceptions.MissingGPSException

private const val TAG = "LocationHandler"

class LocationHandler(private val activity: Activity) {

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(activity) }
    private val cancelationToken by lazy { CancellationTokenSource() }

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation() {
        if (!isGPSEnabled()) throw MissingGPSException()
        val temp = fusedLocationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, cancelationToken.token)

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, cancelationToken.token)
            .addOnSuccessListener { Log.d("bbb", "Dobio sam trenutnu lokaciju: $it ") }
            .addOnFailureListener { Log.e("bbb", "Error happened during current location fetch", it) }
    }


    private fun isGPSEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
}
