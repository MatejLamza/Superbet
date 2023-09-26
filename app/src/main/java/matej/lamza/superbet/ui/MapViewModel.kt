package matej.lamza.superbet.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import matej.lamza.core_data.repository.BetshopRepository

private val COORDS = listOf<Double>(48.16124, 11.60912, 48.12229, 11.52741)

class MapViewModel(private val betshopRepository: BetshopRepository) : ViewModel() {

    val temp = betshopRepository.fetchAllBetshopsForGivenLocation(COORDS, onStart = {}, onComplete = {}, onError = {
        Log.d("bbb", "Error: $it")
    }).asLiveData()


}
