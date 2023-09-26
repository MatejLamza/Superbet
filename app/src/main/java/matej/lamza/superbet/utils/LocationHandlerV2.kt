package matej.lamza.superbet.utils

import android.app.Activity
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task


class LocationHandlerV2(activity: Activity) {

    private val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(createLocationRequest())

    private val client: SettingsClient = LocationServices.getSettingsClient(activity)
    private val locationSettingsTask: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(activity) }
    private val cancelationToken by lazy { CancellationTokenSource() }

    private fun createLocationRequest() =
        LocationRequest
            .Builder(Priority.PRIORITY_PASSIVE, 0)
            .build()


}
