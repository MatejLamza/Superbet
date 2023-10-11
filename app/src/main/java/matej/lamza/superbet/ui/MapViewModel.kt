package matej.lamza.superbet.ui

import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.VisibleRegion
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.asBindingProperty
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import matej.lamza.core_data.repository.BetshopRepository
import matej.lamza.core_model.Betshop

fun Double.round(decimals: Int = 5): Double = "%.${decimals}f".format(this).toDouble()

class MapViewModel(private val betshopRepository: BetshopRepository) : BindingViewModel() {

    private var processCameraJob: Job? = null

    private val _visibleBetshops = MutableLiveData<List<Betshop>>()
    val visibleBetshops: LiveData<List<Betshop>> = _visibleBetshops

    private val _selectedBetshop = MutableStateFlow<Betshop?>(null)

    @get:Bindable
    val betShop: Betshop? by _selectedBetshop.asBindingProperty()

    fun selectBetShop(betShops: Collection<Betshop>, marker: Marker?) {
        viewModelScope.launch {
            if (marker == null) {
                _selectedBetshop.value = null
                return@launch
            }
            val selectedBetShop = getSelectedBetShop(betShops, marker)
            _selectedBetshop.emit(selectedBetShop)
        }
    }

    private fun getSelectedBetShop(betShops: Collection<Betshop>, marker: Marker): Betshop? {
        return betShops.find { it.position == marker.position }
    }

    /**
     * top-right latitude (lat1),
     * top-right longitude (lon1),
     * bottom-left latitude (lat2),
     * bottom-left longitude (lon2)"
     */
    fun processCameraMovement(visibleRegion: VisibleRegion) {
        if (processCameraJob != null && processCameraJob!!.isActive) processCameraJob!!.cancel()
        processCameraJob = viewModelScope.launch {
            betshopRepository.fetchAllBetshopsForGivenLocation(listOf(
                visibleRegion.farRight.latitude.round(),
                visibleRegion.farRight.longitude.round(),
                visibleRegion.nearLeft.latitude.round(),
                visibleRegion.nearLeft.longitude.round(),
            ), {}, {}, {}).onCompletion { Log.d(TAG, "Current visible region: ${visibleRegion}") }
                .collectLatest { _visibleBetshops.value = it }
        }
    }
}
