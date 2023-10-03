package matej.lamza.superbet.utils.maps

import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.ScreenBasedAlgorithm
import kotlinx.coroutines.flow.StateFlow
import matej.lamza.core_model.MapMarkerState

interface ClusterManagerService<T : ClusterItem> {

    val markerStateFlow: StateFlow<MapMarkerState>

    fun setupClusterManager(algorithm: ScreenBasedAlgorithm<T>? = null): ClusterManager<T>

    fun updateMarkerState(currentlySelectedMarker: Marker?)

}
