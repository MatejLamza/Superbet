package matej.lamza.superbet.utils.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.asDeferred
import matej.lamza.core_model.exceptions.MissingGPSException
import matej.lamza.superbet.utils.extensions.isGPSEnabled


private const val TAG = "DefaultLocationClient"

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    private val cancellationToken by lazy { CancellationTokenSource() }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Flow<Location> {
        return flow {
            emit(
                client
                    .getCurrentLocation(Priority.PRIORITY_PASSIVE, cancellationToken.token)
                    .asDeferred()
                    .await()
            )
        }.onStart { checkIfGPSIsEnabled() } //Before providing location check if GPS is enabled
    }

    override fun checkIfGPSIsEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isGPSEnabled) throw MissingGPSException()
        return true
    }

}
