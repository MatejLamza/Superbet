package matej.lamza.superbet.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import matej.lamza.core_model.exceptions.MissingGPSException
import matej.lamza.superbet.utils.extensions.isGPSEnabled


private const val TAG = "DefaultLocationClient"

class DefaultLocationClient(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient
) : LocationClient {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Flow<Location> {
        return callbackFlow {
            locationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location -> if (location != null) trySend(location) }
            awaitClose { locationClient.flushLocations() }
        }
            .onStart { throw IllegalStateException("GPS IS Disabled stopping flow") }
    }

    override fun checkIfGPSIsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isGPSEnabled) throw MissingGPSException()
        return true
    }
}
