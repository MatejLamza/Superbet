package matej.lamza.superbet.utils.maps

import android.app.Activity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import matej.lamza.core_model.Betshop
import matej.lamza.core_model.MapMarkerState
import matej.lamza.superbet.utils.extensions.setInactive
import matej.lamza.superbet.utils.extensions.setStateIcon

class BetshopClusterManager(
    private val activity: Activity, private val googleMap: GoogleMap, private val externalScope: CoroutineScope
) : ClusterManagerService<Betshop> {

    private val _markerStateFlow = MutableStateFlow<MapMarkerState>(MapMarkerState.Inactive())
    private val _selectedBetshop = MutableStateFlow<Betshop?>(null)

    override val markerStateFlow = _markerStateFlow.onEach { it.setStateIcon() }
        .stateIn(externalScope, SharingStarted.WhileSubscribed(), _markerStateFlow.value)
    override val selectedBetshop: StateFlow<Betshop?>
        get() = _selectedBetshop.stateIn(externalScope, SharingStarted.WhileSubscribed(), _selectedBetshop.value)


    //    algorithm = NonHierarchicalViewBasedAlgorithm(screenDimensions.width, screenDimensions.height)
    override fun setupClusterManager(): com.google.maps.android.clustering.ClusterManager<Betshop> {
        return com.google.maps.android.clustering.ClusterManager<Betshop>(activity, googleMap).apply {
            renderer = BetshopClusterRenderer(activity, googleMap, this)
        }
    }

    // todo cleanup, set inactive part is not correctly handled this function now has 2 jobs to update marker state
    // and change marker manually
    override fun updateMarkerState(currentlySelectedMarker: Marker?) {
        if (currentlySelectedMarker != null && _markerStateFlow.value.marker == null) {
            // No marker is selected yet
            _markerStateFlow.value = MapMarkerState.Active(currentlySelectedMarker)
        } else if (_markerStateFlow.value.marker != null && currentlySelectedMarker != null) {
            // Marker already selected check if user clicked on same marker
            _markerStateFlow.value = if (_markerStateFlow.value.marker == currentlySelectedMarker) {
                // User selected same marker, unselect it
                MapMarkerState.Inactive(currentlySelectedMarker)
            } else {
                // unselect previous marker and select new one
                _markerStateFlow.value.marker?.setInactive()
                MapMarkerState.Active(currentlySelectedMarker)
            }
        }
    }

    override fun updateCurrentlySelectedBetshop(clusterManager: ClusterManager<Betshop>, marker: Marker?) {
        if (marker == null) {
            _selectedBetshop.value = null
            return
        }
        _selectedBetshop.value = clusterManager.algorithm.items.find { it.position == marker?.position }
    }
}
