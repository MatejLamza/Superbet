package matej.lamza.superbet.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


private const val TAG = "DefaultLocationClient"

class DefaultLocationClient(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient
) : LocationClient {

    private val client: SettingsClient = LocationServices.getSettingsClient(context)
    private val builder = LocationSettingsRequest
        .Builder()
        .addLocationRequest(createLocationRequest())
        .setAlwaysShow(true)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Flow<Location> {
        return callbackFlow {
            locationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location -> if (location != null) trySend(location) }

            awaitClose { locationClient.flushLocations() }
        }.onStart { checkIfGPSIsEnabled() }
    }

    override suspend fun checkIfGPSIsEnabled(): Boolean {
        return suspendCoroutine { continuation ->
            client.checkLocationSettings(builder.build())
                .addOnSuccessListener {
                    continuation.resume(true)
                }
                .addOnFailureListener {
                    continuation.resumeWithException(it)
                }
        }
    }

    private fun createLocationRequest() =
        LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .build()
}
