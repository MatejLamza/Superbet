package matej.lamza.superbet.utils.maps

import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import kotlinx.coroutines.flow.StateFlow
import matej.lamza.core_model.Betshop
import matej.lamza.core_model.MapMarkerState

//todo rename this its not a service
interface ClusterManagerService<T : ClusterItem> {

    val markerStateFlow: StateFlow<MapMarkerState>

    val selectedBetshop: StateFlow<Betshop?>

    fun setupClusterManager(): ClusterManager<T>

    fun updateMarkerState(currentlySelectedMarker: Marker?)

    fun updateCurrentlySelectedBetshop(clusterManager: ClusterManager<T>, marker: Marker?)

}
