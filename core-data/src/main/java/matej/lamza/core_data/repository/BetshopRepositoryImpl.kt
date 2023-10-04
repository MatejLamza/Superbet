package matej.lamza.core_data.repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onFailure
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import matej.lamza.core_model.Betshop
import matej.lamza.core_model.mapper.asDomain
import matej.lamza.core_network.service.BetshopService

private const val TAG = "BetshopRepository"

class BetshopRepositoryImpl(private val betshopService: BetshopService) : BetshopRepository {

    @WorkerThread
    override fun fetchAllBetshopsForGivenLocation(
        cords: List<Double>, onStart: () -> Unit, onComplete: () -> Unit, onError: (String?) -> Unit
    ): Flow<List<Betshop>> = flow {
        val parameter = cords.joinToString(separator = ",").trim()
        Log.d(TAG, "Given cords: $parameter:")
        val response = betshopService.fetchBetshops(parameter)
        response.suspendOnSuccess {
            val data = data.betshops.asDomain()
            Log.d(TAG, "Data from API: ${data.size}")
            emit(data)
        }
            .onFailure {
                Log.e(TAG, "Erorr happend ${this.message()} ")
                onError(message())
            }
    }
        .onStart { onStart() }
        .onCompletion { onComplete() }
        .flowOn(Dispatchers.IO)
}
