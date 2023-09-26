package matej.lamza.core_data.repository

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

class BetshopRepositoryImpl(private val betshopService: BetshopService) : BetshopRepository {

    @WorkerThread
    override fun fetchAllBetshopsForGivenLocation(
        cords: List<Double>, onStart: () -> Unit, onComplete: () -> Unit, onError: (String?) -> Unit
    ): Flow<List<Betshop>> = flow {
        val response = betshopService.fetchBetshops(cords.joinToString(separator = ","))
        response.suspendOnSuccess {
            emit(data.betshops.asDomain())
        }.onFailure {
            onError(message())
        }
    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(Dispatchers.IO)
}
