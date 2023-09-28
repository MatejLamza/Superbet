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
    private val settingsBuilder = LocationSettingsRequest
        .Builder()
        .addLocationRequest(createLocationRequest())
        .setAlwaysShow(true)

    /**
     * Permissions are already checked before this step.
     * [onStart] will check weather the user has enabled location services before starting the flow and asking
     * for current location. In case location services are enabled flow will continue and in case of missing location
     * services exception will be thrown.
     *
     */
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Flow<Location> {
        return callbackFlow {
            locationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location -> if (location != null) trySend(location) }

            awaitClose { locationClient.flushLocations() }

        }.onStart { checkIfGPSIsEnabled() }
    }

    /**
     * @return [Boolean] if user have GPS setting enabled.
     * @throws ResolvableApiException depending on [Priority] flag.
     *
     */
    override suspend fun checkIfGPSIsEnabled(): Boolean {
        return suspendCoroutine { continuation ->
            client.checkLocationSettings(settingsBuilder.build())
                .addOnSuccessListener { continuation.resume(true) }
                .addOnFailureListener { continuation.resumeWithException(it) }
        }
    }

    /**
     * In case [Priority.PRIORITY_HIGH_ACCURACY] [client] will throw an exception if user disabled GPS.
     * Its important to have flag set to [Priority.PRIORITY_HIGH_ACCURACY] because
     * we can use exception that [checkIfGPSIsEnabled] throws and prompt user to turn on location services using
     * [ResolvableApiException.startResolutionForResult]
     */
    private fun createLocationRequest() =
        LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
            .build()
}
