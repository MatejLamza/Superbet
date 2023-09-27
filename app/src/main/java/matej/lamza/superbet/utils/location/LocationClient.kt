package matej.lamza.superbet.utils.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    suspend fun getCurrentLocation(): Flow<Location>

    suspend fun checkIfGPSIsEnabled(): Boolean
}
