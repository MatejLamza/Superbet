package matej.lamza.superbet.utils.maps

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import matej.lamza.core_model.Betshop
import matej.lamza.superbet.R

class BetshopClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<Betshop>?) :
    DefaultClusterRenderer<Betshop>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: Betshop, markerOptions: MarkerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin_normal))
    }
}
