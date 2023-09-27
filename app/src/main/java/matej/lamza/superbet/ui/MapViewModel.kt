package matej.lamza.superbet.ui

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import matej.lamza.core_data.repository.BetshopRepository

private val COORDS = listOf<Double>(48.16124, 11.60912, 48.12229, 11.52741)

class MapViewModel(
    private val betshopRepository: BetshopRepository,
) : ViewModel() {

    val temp = betshopRepository.fetchAllBetshopsForGivenLocation(COORDS, onStart = {}, onComplete = {}, onError = {
        Log.d("bbb", "Error: $it")
    }).asLiveData()

    fun requestCurrentLocation(locationTask: Task<Location>) {
        viewModelScope.launch {
            locationTask
                .addOnSuccessListener {

                }
                .addOnFailureListener {

                }
        }
    }

}
