package matej.lamza.superbet.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.VisibleRegion
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import matej.lamza.core_data.repository.BetshopRepository
import matej.lamza.core_model.Betshop

fun Double.round(decimals: Int = 5): Double = "%.${decimals}f".format(this).toDouble()

class MapViewModel(private val betshopRepository: BetshopRepository) : ViewModel() {

    private var processCameraJob: Job? = null

    private val _visibleBetshops = MutableLiveData<List<Betshop>>()
    val visibleBetshops: LiveData<List<Betshop>> = _visibleBetshops

    /**
     * top-right latitude (lat1),
     * top-right longitude (lon1),
     * bottom-left latitude (lat2),
     * bottom-left longitude (lon2)"
     */
    @OptIn(FlowPreview::class)
    fun processCameraMovement(visibleRegion: VisibleRegion) {
        if (processCameraJob != null && processCameraJob!!.isActive) processCameraJob!!.cancel()
        processCameraJob = viewModelScope.launch {
            betshopRepository.fetchAllBetshopsForGivenLocation(
                listOf(
                    visibleRegion.farRight.latitude,
                    visibleRegion.farRight.longitude,
                    visibleRegion.nearLeft.latitude,
                    visibleRegion.nearLeft.longitude,
                ), {}, {}, {})
                .collectLatest { _visibleBetshops.value = it }
            Log.d(TAG, "Current visible region: ${visibleRegion}")
        }
    }
}
