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
import matej.lamza.superbet.utils.extensions.setStateIcon

/**
 * This class is implementation of [ClusterManagerService] which is designed to handle
 * initialization and setup of ClusterManager along with keeping track of [ClusterManager]
 * jobs such as updating markers, keeping track of currently selected markers etc.
 * Because we are working with context based classes such as [Activity] this is where
 * we are holding our logic based code.
 *
 * @param activity reference to activity that implements the [GoogleMap] and [ClusterManager]
 * @param googleMap reference to [GoogleMap] handle which is needed to instantiate [ClusterManager]
 * @param externalScope is scope in which  [StateFlow.stateIn] is started.
 */

class BetshopClusterManager(
    private val activity: Activity, private val googleMap: GoogleMap, private val externalScope: CoroutineScope
) : ClusterManagerService<Betshop> {

    private val _markerStateFlow = MutableStateFlow<MapMarkerState>(MapMarkerState.Inactive())

    override val markerStateFlow = _markerStateFlow
        .onEach { it.setStateIcon() }
        .stateIn(externalScope, SharingStarted.WhileSubscribed(), _markerStateFlow.value)

    override fun setupClusterManager(): ClusterManager<Betshop> {
        return ClusterManager<Betshop>(activity, googleMap).apply {
            renderer = BetshopClusterRenderer(activity, googleMap, this)
        }
    }

    override fun updateMarkerState(currentlySelectedMarker: Marker?) {
        if (currentlySelectedMarker != null && _markerStateFlow.value.marker == null) {
            // No marker is selected yet
            _markerStateFlow.value = MapMarkerState.Active(currentlySelectedMarker)
        } else if (currentlySelectedMarker != null && _markerStateFlow.value is MapMarkerState.Inactive) {
            //if user clicks on last inactive marker
            _markerStateFlow.value = MapMarkerState.Active(currentlySelectedMarker)
        } else if (_markerStateFlow.value.marker != null && currentlySelectedMarker != null) {
            // Marker already selected check if user clicked on same marker
            if (_markerStateFlow.value.marker == currentlySelectedMarker) {
                // User selected same marker, unselect it
                _markerStateFlow.value = MapMarkerState.Inactive(currentlySelectedMarker)
            } else {
                // unselect previous marker and select new one
                _markerStateFlow.value = MapMarkerState.Inactive(_markerStateFlow.value.marker)
                _markerStateFlow.value = MapMarkerState.Active(currentlySelectedMarker)
            }
        } else if (currentlySelectedMarker == null) {
            //if user clicks on map but not on marker we want to hide active marker and set it to inactive
            _markerStateFlow.value = MapMarkerState.Inactive(_markerStateFlow.value.marker)
            _markerStateFlow.value = MapMarkerState.Inactive(null)
        }
    }

    override fun createCluster(clusterManager: ClusterManager<Betshop>, dataset: Collection<Betshop>) {
        clusterManager.clearItems()
        clusterManager.addItems(dataset)
        clusterManager.cluster()
    }

}
