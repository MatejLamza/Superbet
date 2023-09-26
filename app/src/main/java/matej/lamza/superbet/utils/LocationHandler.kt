package matej.lamza.superbet.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource

class LocationHandler(private val activity: Activity) {

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(activity) }
    private val cancelationToken by lazy { CancellationTokenSource() }
    private val client: SettingsClient = LocationServices.getSettingsClient(activity)


    @SuppressLint("MissingPermission")
    fun requestCurrentLocation() {
        //check if LOCATION IS ENABLED
        Log.d("bbb", "Requesting current location... ")
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_LOW_POWER, cancelationToken.token)
            .addOnSuccessListener {
                Log.d("bbb", "Dobio sam trenutnu lokaciju: $it ")
            }
            .addOnFailureListener {
                Log.e("bbb", "Error happened during current location fetch", it)
            }
    }


    fun isGPSEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }
}
