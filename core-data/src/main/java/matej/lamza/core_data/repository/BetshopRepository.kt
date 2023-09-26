package matej.lamza.core_data.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import matej.lamza.core_model.Betshop

interface BetshopRepository {

    @WorkerThread
    fun fetchAllBetshopsForGivenLocation(
        cords: List<Double>,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ): Flow<List<Betshop>>
}
