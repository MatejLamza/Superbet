package matej.lamza.superbet.ui

import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.VisibleRegion
import com.skydoves.bindables.BindingViewModel
import com.skydoves.bindables.bindingProperty
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
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

    @get:Bindable
    var betshop: Betshop? by bindingProperty(null)

    fun updateSelectedBetshop(selectedBetshop: Betshop?) {
        if (selectedBetshop != null) betshop = selectedBetshop
    }

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
                .onCompletion { Log.d(TAG, "Current visible region: ${visibleRegion}") }
                .collectLatest { _visibleBetshops.value = it }
        }
    }
}
